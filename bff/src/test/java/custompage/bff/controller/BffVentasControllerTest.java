package custompage.bff.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import custompage.bff.config.RestTemplateTestConfig;
import custompage.bff.dto.OrdenBffDTO;
import custompage.bff.service.IBffOrchestratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BffVentasController.class)
@Import(RestTemplateTestConfig.class)
class BffVentasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IBffOrchestratorService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void emitirOrden_Success() throws Exception {
        OrdenBffDTO dto = new OrdenBffDTO();
        when(service.delegarCreacionOrden(any(OrdenBffDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/bff/ventas/orden")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void procesarPago_Success() throws Exception {
        OrdenBffDTO dto = new OrdenBffDTO();
        when(service.delegarPagoVenta(eq(1L), eq("TARJETA"))).thenReturn(dto);

        mockMvc.perform(post("/api/bff/ventas/orden/1/pagar")
                        .param("metodoPago", "TARJETA"))
                .andExpect(status().isOk());
    }
}