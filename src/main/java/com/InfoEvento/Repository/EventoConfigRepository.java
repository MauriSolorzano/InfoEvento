package com.InfoEvento.Repository;

import com.InfoEvento.Entity.EventoConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoConfigRepository extends JpaRepository<EventoConfig, Long> {
}
