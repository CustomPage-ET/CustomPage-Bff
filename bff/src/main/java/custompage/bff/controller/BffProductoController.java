package custompage.bff.controller;

import custompage.bff.dto.ProductoBffDTO;
import custompage.bff.service.IBffOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/productos")
public class BffProductoController {

    private final IBffOrchestratorService service;

    public BffProductoController(IBffOrchestratorService service) {
        this.service = service;
    }

    // Endpoint para guardar nuevo producto o actualizar stock/precio
    @PostMapping
    public ResponseEntity<ProductoBffDTO> guardarOModificarProducto(@Validated @RequestBody ProductoBffDTO dto) {
        return ResponseEntity.ok(service.crearOActualizarProducto(dto));
    }

    // Endpoint para sacar un producto del catálogo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarProducto(@PathVariable Long id) {
        service.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}