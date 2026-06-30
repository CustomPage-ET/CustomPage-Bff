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
        // 1. Llamada al microservicio de Configuración Estética
        String urlTiendaConfig = urlConfig + "/tienda/" + idEmpresa;
        DashboardTiendaDTO configResponse = restTemplate.getForObject(urlTiendaConfig, DashboardTiendaDTO.class);

        // 2. Llamada en paralelo lógico al catálogo de Productos de la PYME
        String urlCatalogo = urlProducts + "/empresa/" + idEmpresa;
        ProductoBffDTO[] productosArray = restTemplate.getForObject(urlCatalogo, ProductoBffDTO[].class);
        List<ProductoBffDTO> productos = Arrays.asList(productosArray);

        // 3. Orquestación combinada
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

    public AuthResponseDTO fallbackLogin(AuthRequestDTO dto, Throwable e) {
        throw new BffIntegrationException("Servicio de autenticación no disponible. Detalles: " + e.getMessage());
    }

    public DashboardTiendaDTO fallbackDashboard(Long idEmpresa, Throwable e) {
        // Fallback elegante: si cae la visual o productos, devolvemos un cascarón por defecto para que la pantalla no muera
        return DashboardTiendaDTO.builder()
                .idEmpresa(idEmpresa)
                .estetica(EsteticaBffDTO.builder().paletaColores("Pastel").fuenteTexto("Roboto").urlLogo("").build())
                .modulosActivos(new ArrayList<>())
                .catalogoProductos(new ArrayList<>())
                .build();
    }

    public OrdenBffDTO fallbackOrden(Throwable e) {
        throw new BffIntegrationException("El módulo de facturación y pasarela de órdenes no responde. Operación cancelada de forma segura.");
    }

    public List<PromocionBffDTO> fallbackPromociones(Throwable e) {
        return new ArrayList<>(); // Si marketing está caído, se muestran 0 cupones activos, pero se deja seguir navegando.
    }
}