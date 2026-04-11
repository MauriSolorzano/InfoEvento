package com.InfoEvento.Controller;

import com.InfoEvento.Entity.Sector;
import com.InfoEvento.Service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visor")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Devuelve los sectores asignados al visor,
    // o todos los activos si no tiene ninguno asignado
    @GetMapping("/mis-sectores")
    public ResponseEntity<?> misSectores(Authentication authentication) {
        try {
            List<Sector> sectores = usuarioService.misSectores(authentication.getName());
            return ResponseEntity.ok(sectores);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
