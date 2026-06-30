package custompage.bff.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardTiendaDTO {
    private Long idEmpresa;
    private EsteticaBffDTO estetica;
    private List<ModuloBffDTO> modulosActivos;
    private List<ProductoBffDTO> catalogoProductos;
}