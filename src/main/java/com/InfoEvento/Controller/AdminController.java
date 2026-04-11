package com.InfoEvento.Controller;

import com.InfoEvento.Entity.Sector;
import com.InfoEvento.Entity.Usuario;
import com.InfoEvento.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ─── SECTORES ─────────────────────────────────────────────

    @GetMapping("/sectores")
    public ResponseEntity<?> listarSectores() {
        return ResponseEntity.ok(adminService.listarSectores());
    }

    @PostMapping("/sectores")
    public ResponseEntity<?> crearSector(@RequestBody Map<String, String> body) {
        try {
            Sector sector = adminService.crearSector(
                    body.get("nombre"),
                    body.get("nombreDisplay")
            );
            return ResponseEntity.ok(sector);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/sectores/{id}")
    public ResponseEntity<?> actualizarSector(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        try {
            Sector sector = adminService.actualizarSector(id, body);
            return ResponseEntity.ok(sector);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/sectores/{id}")
    public ResponseEntity<?> eliminarSector(@PathVariable Long id) {
        try {
            adminService.eliminarSector(id);
            return ResponseEntity.ok(Map.of("message", "Sector eliminado", "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/publico/sectores")
    public ResponseEntity<?> sectoresPublicos(){
        return ResponseEntity.ok(adminService.listarSectores());
    }

    @GetMapping("/publico/velocidad-carrusel")
    public ResponseEntity<?> velocidadCarruselPublica() {
        return ResponseEntity.ok(Map.of("velocidad", adminService.getVelocidadCarrusel()));
    }

    // ─── AUTO-APROBACIÓN GLOBAL ───────────────────────────────

    @GetMapping("/configuracion/auto-aprobacion")
    public ResponseEntity<?> getAutoAprobacion() {
        return ResponseEntity.ok(Map.of("autoAprobacion", adminService.getAutoAprobacion()));
    }

    @PutMapping("/configuracion/auto-aprobacion")
    public ResponseEntity<?> setAutoAprobacion(@RequestBody Map<String, Boolean> body) {
        try {
            boolean nuevoValor = body.get("autoAprobacion");
            adminService.setAutoAprobacion(nuevoValor);
            return ResponseEntity.ok(Map.of("autoAprobacion", nuevoValor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/configuracion/velocidad-carrusel")
    public ResponseEntity<?> getVelocidadCarrusel() {
        return ResponseEntity.ok(Map.of("velocidad", adminService.getVelocidadCarrusel()));
    }

    @PutMapping("/configuracion/velocidad-carrusel")
    public ResponseEntity<?> setVelocidadCarrusel(@RequestBody Map<String, Integer> body) {
        try {
            adminService.setVelocidadCarrusel(body.get("velocidad"));
            return ResponseEntity.ok(Map.of("velocidad", body.get("velocidad")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── USUARIOS ─────────────────────────────────────────────

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, Object> body) {
        try {
            Usuario usuario = adminService.crearUsuario(body);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                               @RequestBody Map<String, Object> body) {
        try {
            Usuario usuario = adminService.actualizarUsuario(id, body);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
