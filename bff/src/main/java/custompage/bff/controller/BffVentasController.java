package custompage.bff.controller;

import custompage.bff.dto.OrdenBffDTO;
import custompage.bff.service.IBffOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/ventas")
public class BffVentasController {
    private final IBffOrchestratorService service;
    public BffVentasController(IBffOrchestratorService service) { this.service = service; }

    @PostMapping("/orden")
    public ResponseEntity<OrdenBffDTO> emitirOrden(@Validated @RequestBody OrdenBffDTO dto) {
        return ResponseEntity.ok(service.delegarCreacionOrden(dto));
    }

    @PostMapping("/orden/{idOrden}/pagar")
    public ResponseEntity<OrdenBffDTO> procesarPago(@PathVariable Long idOrden, @RequestParam String metodoPago) {
        return ResponseEntity.ok(service.delegarPagoVenta(idOrden, metodoPago));
    }
}