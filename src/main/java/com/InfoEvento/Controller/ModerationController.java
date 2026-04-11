package com.InfoEvento.Controller;

import com.InfoEvento.Service.ModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/moderacion")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    // Ver toda la cola de pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<?> listarPendientes() {
        return ResponseEntity.ok(moderationService.listarPendientes());
    }

    // Ver pendientes filtradas por sector
    @GetMapping("/pendientes/sector/{sectorId}")
    public ResponseEntity<?> listarPendientesPorSector(@PathVariable Long sectorId) {
        return ResponseEntity.ok(moderationService.listarPendientesPorSector(sectorId));
    }

    // Aprobar una imagen individual
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarImagen(@PathVariable Long id) {
        try {
            moderationService.aprobarImagen(id);
            return ResponseEntity.ok(Map.of("message", "Imagen aprobada", "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Rechazar (elimina del storage y la BD)
    @DeleteMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarImagen(@PathVariable Long id) {
        try {
            moderationService.rechazarImagen(id);
            return ResponseEntity.ok(Map.of("message", "Imagen rechazada y eliminada", "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Aprobar todas las pendientes de un sector de una vez
    @PutMapping("/sector/{sectorId}/aprobar-todas")
    public ResponseEntity<?> aprobarTodasDelSector(@PathVariable Long sectorId) {
        try {
            int cantidad = moderationService.aprobarTodasPorSector(sectorId);
            return ResponseEntity.ok(Map.of(
                    "message", "Imágenes aprobadas",
                    "cantidad", cantidad
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
