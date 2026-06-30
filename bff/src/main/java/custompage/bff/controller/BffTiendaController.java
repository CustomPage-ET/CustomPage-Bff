package custompage.bff.controller;

import custompage.bff.dto.DashboardTiendaDTO;
import custompage.bff.service.IBffOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/tienda")
public class BffTiendaController {
    private final IBffOrchestratorService service;
    public BffTiendaController(IBffOrchestratorService service) { this.service = service; }

    @GetMapping("/{idEmpresa}")
    public ResponseEntity<DashboardTiendaDTO> cargarTiendaSaaS(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(service.orquestarDashboardTienda(idEmpresa));
    }
}