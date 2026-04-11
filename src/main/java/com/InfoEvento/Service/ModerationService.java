package com.InfoEvento.Service;

import com.InfoEvento.Entity.Imagen;
import com.InfoEvento.Repository.ImagenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModerationService {
    private final ImagenRepository imagenRepository;
    private final MinioStorageService storageService;
    private final SectorNotificationService notificationService;

    public ModerationService(ImagenRepository imagenRepository,
                             MinioStorageService storageService,
                             SectorNotificationService notificationService) {
        this.imagenRepository = imagenRepository;
        this.storageService = storageService;
        this.notificationService = notificationService;
    }

    public List<Imagen> listarPendientes() {
        return imagenRepository.findByAprobadaFalseOrderBySubidaEnAsc();
    }

    public List<Imagen> listarPendientesPorSector(Long sectorId) {
        return imagenRepository.findBySectorIdAndAprobadaFalseOrderBySubidaEnAsc(sectorId);
    }

    public void aprobarImagen(Long id) {
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada: " + id));

        imagen.setAprobada(true);
        imagenRepository.save(imagen);
        notificationService.notificarCambio(imagen.getSector().getNombre());
    }

    public void rechazarImagen(Long id) {
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada: " + id));

        storageService.eliminarArchivo(imagen.getStoragePath());
        imagenRepository.delete(imagen);
    }

    public int aprobarTodasPorSector(Long sectorId) {
        List<Imagen> pendientes = imagenRepository
                .findBySectorIdAndAprobadaFalseOrderBySubidaEnAsc(sectorId);

        pendientes.forEach(img -> img.setAprobada(true));
        imagenRepository.saveAll(pendientes);

        if (!pendientes.isEmpty()) {
            notificationService.notificarCambio(
                    pendientes.get(0).getSector().getNombre()
            );
        }

        return pendientes.size();
    }
}
