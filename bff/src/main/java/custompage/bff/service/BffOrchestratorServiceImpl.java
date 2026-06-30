package custompage.bff.service;

import custompage.bff.dto.*;
import custompage.bff.exception.BffIntegrationException;
import custompage.bff.service.IBffOrchestratorService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BffOrchestratorServiceImpl implements IBffOrchestratorService {

    private final RestTemplate restTemplate;

    @Value("${urls.auth}") private String urlAuth;
    @Value("${urls.products}") private String urlProducts;
    @Value("${urls.marketing}") private String urlMarketing;
    @Value("${urls.sales}") private String urlSales;
    @Value("${urls.config}") private String urlConfig;

    public BffOrchestratorServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @CircuitBreaker(name = "backendEcosistema", fallbackMethod = "fallbackLogin")
    public AuthResponseDTO delegarLogin(AuthRequestDTO dto) {
        return restTemplate.postForObject(urlAuth + "/login", dto, AuthResponseDTO.class);
    }

    @Override
    @CircuitBreaker(name = "backendEcosistema", fallbackMethod = "fallbackDashboard")
    public DashboardTiendaDTO orquestarDashboardTienda(Long idEmpresa) {
        // 1. Llamar al microservicio de Configuración Estética (SaaS)
        String urlTiendaConfig = urlConfig + "/tienda/" + idEmpresa;
        DashboardTiendaDTO configResponse = restTemplate.getForObject(urlTiendaConfig, DashboardTiendaDTO.class);

        // 2. Llamar al catálogo de Productos de la PYME de Cosméticos
        String urlCatalogo = urlProducts + "/empresa/" + idEmpresa;
        ProductoBffDTO[] productosArray = restTemplate.getForObject(urlCatalogo, ProductoBffDTO[].class);
        List<ProductoBffDTO> productos = Arrays.asList(productosArray);

        // 3. Consolidar la respuesta unificada para el Frontend
        return DashboardTiendaDTO.builder()
                .idEmpresa(idEmpresa)
                .estetica(configResponse.getEstetica())
                .modulosActivos(configResponse.getModulosActivos())
                .catalogoProductos(productos)
                .build();
    }

    @Override
    @CircuitBreaker(name = "backendEcosistema", fallbackMethod = "fallbackOrden")
    public OrdenBffDTO delegarCreacionOrden(OrdenBffDTO dto) {
        return restTemplate.postForObject(urlSales, dto, OrdenBffDTO.class);
    }

    @Override
    @CircuitBreaker(name = "backendEcosistema", fallbackMethod = "fallbackOrden")
    public OrdenBffDTO delegarPagoVenta(Long idOrden, String metodoPago) {
        String urlPago = urlSales + "/" + idOrden + "/pagar?metodoPago=" + metodoPago;
        return restTemplate.postForObject(urlPago, null, OrdenBffDTO.class);
    }

    @Override
    @CircuitBreaker(name = "backendEcosistema", fallbackMethod = "fallbackPromociones")
    public List<PromocionBffDTO> obtenerPromocionesMarketing() {
        PromocionBffDTO[] promos = restTemplate.getForObject(urlMarketing + "/promociones", PromocionBffDTO[].class);
        return Arrays.asList(promos);
    }

    @Override
    public PromocionBffDTO validarCupon(String codigo) {
        return restTemplate.getForObject(urlMarketing + "/cupones/" + codigo, PromocionBffDTO.class);
    }

    @Override
    @CircuitBreaker(name = "backendEcosistema", fallbackMethod = "fallbackProductoAccion")
    public ProductoBffDTO crearOActualizarProducto(ProductoBffDTO dto) {
        return restTemplate.postForObject(urlProducts, dto, ProductoBffDTO.class);
    }

    @Override
    @CircuitBreaker(name = "backendEcosistema", fallbackMethod = "fallbackProductoVoid")
    public void eliminarProducto(Long idProducto) {
        String urlDelete = urlProducts + "/" + idProducto;
        restTemplate.delete(urlDelete);
    }

    public AuthResponseDTO fallbackLogin(AuthRequestDTO dto, Throwable e) {
        throw new BffIntegrationException("El portal de accesos no está disponible en este momento. Intente más tarde.");
    }

    public DashboardTiendaDTO fallbackDashboard(Long idEmpresa, Throwable e) {
        return DashboardTiendaDTO.builder()
                .idEmpresa(idEmpresa)
                .estetica(EsteticaBffDTO.builder().paletaColores("Default/Pastel").fuenteTexto("Montserrat").urlLogo("").build())
                .modulosActivos(new ArrayList<>())
                .catalogoProductos(new ArrayList<>())
                .build();
    }

    public OrdenBffDTO fallbackOrden(Throwable e) {
        throw new BffIntegrationException("La pasarela de cobros y órdenes está en mantenimiento. Su cuenta no ha sufrido cargos.");
    }

    public List<PromocionBffDTO> fallbackPromociones(Throwable e) {
        return new ArrayList<>(); // Si marketing falla, cargamos 0 banners, pero el e-commerce sigue vendiendo
    }

    public ProductoBffDTO fallbackProductoAccion(ProductoBffDTO dto, Throwable e) {
        throw new BffIntegrationException("Imposible procesar la modificación del inventario. El microservicio de productos está inaccesible.");
    }

    public void fallbackProductoVoid(Long idProducto, Throwable e) {
        throw new BffIntegrationException("No se pudo dar de baja el producto cosmético. Error de comunicación con almacenes.");
    }
}