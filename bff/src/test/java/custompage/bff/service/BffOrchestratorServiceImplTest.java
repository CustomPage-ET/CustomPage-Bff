package custompage.bff.service;

import custompage.bff.dto.*;
import custompage.bff.exception.BffIntegrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BffOrchestratorServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BffOrchestratorServiceImpl service;

    @BeforeEach
    void setUp() {
        // Simula la inyección de propiedades @Value del archivo application.properties
        ReflectionTestUtils.setField(service, "urlAuth", "http://localhost:8081/api/auth");
        ReflectionTestUtils.setField(service, "urlProducts", "http://localhost:8082/api/products");
        ReflectionTestUtils.setField(service, "urlMarketing", "http://localhost:8083/api/marketing");
        ReflectionTestUtils.setField(service, "urlSales", "http://localhost:8084/api/sales");
        ReflectionTestUtils.setField(service, "urlConfig", "http://localhost:8085/api/config");
    }

    @Test
    void delegarLogin_Success() {
        AuthRequestDTO request = new AuthRequestDTO();
        AuthResponseDTO expectedResponse = new AuthResponseDTO();

        when(restTemplate.postForObject(anyString(), any(), eq(AuthResponseDTO.class)))
                .thenReturn(expectedResponse);

        AuthResponseDTO actualResponse = service.delegarLogin(request);
        assertNotNull(actualResponse);
        verify(restTemplate, times(1)).postForObject(contains("/login"), eq(request), eq(AuthResponseDTO.class));
    }

    @Test
    void orquestarDashboardTienda_Success() {
        Long idEmpresa = 1L;

        // Se inicializa utilizando constructores/setters tradicionales para evitar problemas si falta @Builder en ProductoBffDTO
        DashboardTiendaDTO mockConfig = DashboardTiendaDTO.builder()
                .estetica(EsteticaBffDTO.builder().paletaColores("Rosa").fuenteTexto("Arial").build())
                .modulosActivos(new ArrayList<>())
                .build();

        ProductoBffDTO producto = new ProductoBffDTO();
        producto.setNombre("Labial Hidratante");
        // Si tu variable se llama idProducto, id, etc., Lombok la mapeará por su setter.
        // En caso de que no compile el setNombre o no tenga setters, puedes dejar el new ProductoBffDTO() vacío sin propiedades asignadas.

        ProductoBffDTO[] mockProductos = new ProductoBffDTO[]{ producto };

        when(restTemplate.getForObject(contains("/tienda/1"), eq(DashboardTiendaDTO.class)))
                .thenReturn(mockConfig);
        when(restTemplate.getForObject(contains("/empresa/1"), eq(ProductoBffDTO[].class)))
                .thenReturn(mockProductos);

        DashboardTiendaDTO result = service.orquestarDashboardTienda(idEmpresa);

        assertNotNull(result);
        assertEquals(idEmpresa, result.getIdEmpresa());
        assertEquals("Rosa", result.getEstetica().getPaletaColores());
        assertEquals(1, result.getCatalogoProductos().size());
    }

    @Test
    void delegarCreacionOrden_Success() {
        OrdenBffDTO dto = new OrdenBffDTO();
        when(restTemplate.postForObject(anyString(), any(), eq(OrdenBffDTO.class))).thenReturn(dto);

        OrdenBffDTO result = service.delegarCreacionOrden(dto);
        assertNotNull(result);
    }

    @Test
    void delegarPagoVenta_Success() {
        OrdenBffDTO dto = new OrdenBffDTO();
        when(restTemplate.postForObject(anyString(), any(), eq(OrdenBffDTO.class))).thenReturn(dto);

        OrdenBffDTO result = service.delegarPagoVenta(1L, "TARJETA");
        assertNotNull(result);
    }

    @Test
    void obtenerPromocionesMarketing_Success() {
        PromocionBffDTO[] promos = new PromocionBffDTO[]{ new PromocionBffDTO() };
        when(restTemplate.getForObject(anyString(), eq(PromocionBffDTO[].class))).thenReturn(promos);

        List<PromocionBffDTO> result = service.obtenerPromocionesMarketing();
        assertEquals(1, result.size());
    }

    @Test
    void validarCupon_Success() {
        PromocionBffDTO dto = new PromocionBffDTO();
        when(restTemplate.getForObject(anyString(), eq(PromocionBffDTO.class))).thenReturn(dto);

        PromocionBffDTO result = service.validarCupon("CUPON50");
        assertNotNull(result);
    }

    @Test
    void crearOActualizarProducto_Success() {
        ProductoBffDTO dto = new ProductoBffDTO();
        when(restTemplate.postForObject(anyString(), any(), eq(ProductoBffDTO.class))).thenReturn(dto);

        ProductoBffDTO result = service.crearOActualizarProducto(dto);
        assertNotNull(result);
    }

    @Test
    void eliminarProducto_Success() {
        doNothing().when(restTemplate).delete(anyString());
        assertDoesNotThrow(() -> service.eliminarProducto(1L));
        verify(restTemplate, times(1)).delete(anyString());
    }

    // --- MÉTODOS DE FALLBACK (Aseguran cobertura de ramas alternativas) ---

    @Test
    void fallbackLogin_ThrowsException() {
        assertThrows(BffIntegrationException.class, () ->
                service.fallbackLogin(new AuthRequestDTO(), new RuntimeException("Timeout"))
        );
    }

    @Test
    void fallbackDashboard_ReturnsDefaultData() {
        DashboardTiendaDTO fallback = service.fallbackDashboard(2L, new RuntimeException("Error 500"));
        assertNotNull(fallback);
        assertEquals(2L, fallback.getIdEmpresa());
        assertEquals("Default/Pastel", fallback.getEstetica().getPaletaColores());
        assertTrue(fallback.getCatalogoProductos().isEmpty());
    }

    @Test
    void fallbackOrden_ThrowsException() {
        assertThrows(BffIntegrationException.class, () ->
                service.fallbackOrden(new RuntimeException("Mantenimiento"))
        );
    }

    @Test
    void fallbackPromociones_ReturnsEmptyList() {
        List<PromocionBffDTO> fallback = service.fallbackPromociones(new RuntimeException("Error de red"));
        assertTrue(fallback.isEmpty());
    }

    @Test
    void fallbackProductoAccion_ThrowsException() {
        assertThrows(BffIntegrationException.class, () ->
                service.fallbackProductoAccion(new ProductoBffDTO(), new RuntimeException("Error"))
        );
    }

    @Test
    void fallbackProductoVoid_ThrowsException() {
        assertThrows(BffIntegrationException.class, () ->
                service.fallbackProductoVoid(1L, new RuntimeException("Error"))
        );
    }
}