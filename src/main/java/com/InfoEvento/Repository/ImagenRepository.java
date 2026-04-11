package com.InfoEvento.Repository;

import com.InfoEvento.Entity.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findBySectorIdAndAprobadaTrueOrderByOrdenAsc(Long sectorId);

    List<Imagen> findByAprobadaFalseOrderBySubidaEnAsc();
    List<Imagen> findBySectorIdAndAprobadaFalseOrderBySubidaEnAsc(Long sectorId);
}
