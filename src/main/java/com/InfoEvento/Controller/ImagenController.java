package com.InfoEvento.Controller;

import com.InfoEvento.Entity.Imagen;
import com.InfoEvento.Service.ImagenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService imagenService;

    public ImagenController(ImagenService imagenService) {
        this.imagenService = imagenService;
    }

    // Público - sin JWT, cualquiera con el QR puede subir
    @PostMapping("/publico")
    public ResponseEntity<?> subirImagenPublica(
            @RequestParam("imagen") MultipartFile archivo,
            @RequestParam("sectorId") Long sectorId) {
        try {
            Imagen imagen = imagenService.guardarImagenPublica(archivo, sectorId);
            return ResponseEntity.ok(Map.of(
                    "message", imagen.getAprobada()
                            ? "Imagen subida y aprobada automáticamente"
                            : "Imagen subida, pendiente de aprobación",
                    "estado", imagen.getAprobada() ? "APROBADA" : "PENDIENTE",
                    "id", imagen.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Protegido - el admin sube imágenes directamente, siempre aprobadas
    @PostMapping
    public ResponseEntity<?> subirImagenAdmin(
            @RequestParam("imagen") MultipartFile archivo,
            @RequestParam("sectorId") Long sectorId,
            Authentication authentication) {
        try {
            Imagen imagen = imagenService.guardarImagenAdmin(
                    archivo, sectorId, authentication.getName()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Imagen subida correctamente",
                    "urlPublica", imagen.getUrlPublica(),
                    "id", imagen.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Público - usado por el televisor para mostrar el carrusel
    @GetMapping("/{sectorId}/aprobadas")
    public ResponseEntity<?> obtenerImagenesAprobadas(@PathVariable Long sectorId) {
        try {
            List<Imagen> imagenes = imagenService.obtenerImagenesAprobadasPorSector(sectorId);
            return ResponseEntity.ok(imagenes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Protegido - el admin elimina cualquier imagen
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarImagen(@PathVariable Long id) {
        try {
            imagenService.eliminarImagenPorId(id);
            return ResponseEntity.ok(Map.of("message", "Imagen eliminada", "id", id));
        } catch (ImagenService.ImagenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
