package custompage.bff.controller;

import custompage.bff.dto.AuthRequestDTO;
import custompage.bff.dto.AuthResponseDTO;
import custompage.bff.service.IBffOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/auth")
public class BffAuthController {
    private final IBffOrchestratorService service;
    public BffAuthController(IBffOrchestratorService service) { this.service = service; }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Validated @RequestBody AuthRequestDTO dto) {
        return ResponseEntity.ok(service.delegarLogin(dto));
    }
}