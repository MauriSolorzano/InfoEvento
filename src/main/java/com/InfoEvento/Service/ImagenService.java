package com.InfoEvento.Service;

import com.InfoEvento.Entity.EventoConfig;
import com.InfoEvento.Entity.Imagen;
import com.InfoEvento.Entity.Sector;
import com.InfoEvento.Repository.EventoConfigRepository;
import com.InfoEvento.Repository.ImagenRepository;
import com.InfoEvento.Repository.SectorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ImagenService {
    private final ImagenRepository imagenRepository;
    private final SectorRepository sectorRepository;
    private final MinioStorageService storageService;
    private final SectorNotificationService notificationService;
    private final EventoConfigRepository eventoConfigRepository;

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf"
    );

    public ImagenService(ImagenRepository imagenRepository,
                         SectorRepository sectorRepository,
                         MinioStorageService storageService,
                         SectorNotificationService notificationService,
                         EventoConfigRepository eventoConfigRepository) {
        this.imagenRepository = imagenRepository;
        this.sectorRepository = sectorRepository;
        this.storageService = storageService;
        this.notificationService = notificationService;
        this.eventoConfigRepository = eventoConfigRepository;
    }

    // ── Subida pública (QR) ────────────────────────────────────
    public Imagen guardarImagenPublica(MultipartFile archivo, Long sectorId) throws Exception {
        validarArchivo(archivo);

        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado: " + sectorId));

        boolean autoAprobacion = getAutoAprobacionActual();
        return guardarArchivoSimple(archivo, sector, null, autoAprobacion);
    }

    // ── Subida directa del admin (siempre aprobada) ────────────
    public Imagen guardarImagenAdmin(MultipartFile archivo, Long sectorId, String username) throws Exception {
        validarArchivo(archivo);

        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado: " + sectorId));

        return guardarArchivoSimple(archivo, sector, username, true);
    }

    // ── Para el televisor: solo aprobadas ─────────────────────
    public List<Imagen> obtenerImagenesAprobadasPorSector(Long sectorId) {
        sectorRepository.findById(sectorId)
                .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado"));
        return imagenRepository.findBySectorIdAndAprobadaTrueOrderByOrdenAsc(sectorId);
    }

    // ── Eliminar ──────────────────────────────────────────────
    public void eliminarImagenPorId(Long id) {
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new ImagenNotFoundException("Imagen no encontrada: " + id));

        storageService.eliminarArchivo(imagen.getStoragePath());
        imagenRepository.delete(imagen);
        notificationService.notificarCambio(imagen.getSector().getNombre());
    }

    // ── Internos ──────────────────────────────────────────────
    private Imagen guardarArchivoSimple(MultipartFile archivo, Sector sector,
                                        String subidaPor, boolean aprobada) throws Exception {
        String urlPublica = storageService.subirArchivo(archivo, sector.getNombre());

        String sectorLimpio = sector.getNombre().toUpperCase().trim().replace(" ", "_");
        String nombreArchivoConUUID = urlPublica.substring(urlPublica.lastIndexOf("/") + 1);
        String storagePath = sectorLimpio + "/" + nombreArchivoConUUID;

        Imagen imagen = new Imagen();
        imagen.setSector(sector);
        imagen.setNombreArchivo(archivo.getOriginalFilename());
        imagen.setStoragePath(storagePath);
        imagen.setUrlPublica(urlPublica);
        imagen.setSubidaEn(LocalDateTime.now());
        imagen.setAprobada(aprobada);
        imagen.setSubidaPor(subidaPor); // null si es público

        Imagen guardada = imagenRepository.save(imagen);

        if (aprobada) {
            notificationService.notificarCambio(sector.getNombre());
        }

        return guardada;
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo.isEmpty())
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        if (!TIPOS_PERMITIDOS.contains(archivo.getContentType()))
            throw new IllegalArgumentException("Tipo de archivo no permitido");
    }

    private boolean getAutoAprobacionActual() {
        return eventoConfigRepository.findById(1L)
                .map(EventoConfig::getAutoAprobacion)
                .orElse(false);
    }

    public class ImagenNotFoundException extends RuntimeException {
        public ImagenNotFoundException(String message) { super(message); }
    }
}
