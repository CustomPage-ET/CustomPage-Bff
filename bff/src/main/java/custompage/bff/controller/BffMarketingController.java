package custompage.bff.controller;

import custompage.bff.dto.PromocionBffDTO;
import custompage.bff.service.IBffOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bff/marketing")
public class BffMarketingController {
    private final IBffOrchestratorService service;
    public BffMarketingController(IBffOrchestratorService service) { this.service = service; }

    @GetMapping("/promociones")
    public ResponseEntity<List<PromocionBffDTO>> listarPromos() {
        return ResponseEntity.ok(service.obtenerPromocionesMarketing());
    }

    @GetMapping("/cupones/{codigo}")
    public ResponseEntity<PromocionBffDTO> verificarCupon(@PathVariable String codigo) {
        return ResponseEntity.ok(service.validarCupon(codigo));
    }
}