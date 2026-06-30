package custompage.bff.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoBffDTO {
    private Long idProducto;
    private String nombre;
    private String codigoSKU;
    private BigDecimal precio;
    private Integer stock;
    private Long idEmpresa;
}