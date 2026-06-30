package custompage.bff.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenBffDTO {
    private Long idOrden;
    private Long idEmpresa;
    private String estado;
    private BigDecimal total;
    private List<DetalleOrdenBffDTO> detalles;
}