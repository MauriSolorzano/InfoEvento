package com.InfoEvento.Repository;

import com.InfoEvento.Entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {
    List<Sector> findByActivoTrue();
    Optional<Sector> findByNombre(String nombre);
}
