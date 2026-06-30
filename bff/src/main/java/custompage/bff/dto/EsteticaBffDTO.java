package custompage.bff.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EsteticaBffDTO {
    private Long idEstetica;
    private Long idEmpresa;
    private String paletaColores;
    private String fuenteTexto;
    private String urlLogo;
    private String urlBannerFondo;
}