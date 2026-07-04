package custompage.bff.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import custompage.bff.config.RestTemplateTestConfig;
import custompage.bff.dto.ProductoBffDTO;
import custompage.bff.service.IBffOrchestratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BffProductoController.class)
@Import(RestTemplateTestConfig.class)
class BffProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IBffOrchestratorService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void guardarOModificarProducto_Success() throws Exception {
        ProductoBffDTO dto = new ProductoBffDTO();
        when(service.crearOActualizarProducto(any(ProductoBffDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/bff/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void borrarProducto_Success() throws Exception {
        doNothing().when(service).eliminarProducto(1L);

        mockMvc.perform(delete("/api/bff/productos/1"))
                .andExpect(status().isNoContent());
    }
}