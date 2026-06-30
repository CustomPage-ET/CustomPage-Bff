package custompage.bff.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ModuloBffDTO {
    private Long idModulo;
    private Long idEmpresa;
    private String nombreModulo;
    private Boolean activo;
    private Integer ordenPantalla;
}