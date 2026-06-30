package custompage.bff.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleOrdenBffDTO {
    private String codigoSKU;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}