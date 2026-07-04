package custompage.bff.exception;

import custompage.bff.config.RestTemplateTestConfig;
import custompage.bff.controller.BffProductoController;
import custompage.bff.service.IBffOrchestratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BffProductoController.class)
@Import(RestTemplateTestConfig.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IBffOrchestratorService service;

    @Test
    void handleIntegrationException_ReturnsBadGateway() throws Exception {
        doThrow(new BffIntegrationException("El microservicio de productos está inaccesible."))
                .when(service).eliminarProducto(anyLong());

        mockMvc.perform(delete("/api/bff/productos/99"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error").value("Fallo de Integración"))
                .andExpect(jsonPath("$.details").value("El microservicio de productos está inaccesible."));
    }

    @Test
    void handleGeneralException_ReturnsInternalServerError() throws Exception {
        doThrow(new NullPointerException("Error interno inesperado"))
                .when(service).eliminarProducto(anyLong());

        mockMvc.perform(delete("/api/bff/productos/99"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error en BFF"))
                .andExpect(jsonPath("$.message").value("Error interno inesperado"));
    }
}