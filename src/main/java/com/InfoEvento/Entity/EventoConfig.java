package com.InfoEvento.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "evento_config")
public class EventoConfig {
    @Id
    private Long id = 1L; // Siempre hay una sola fila

    @Column(name = "auto_aprobacion", nullable = false)
    private Boolean autoAprobacion = false;

    @Column(name = "velocidad_carrusel", nullable = false)
    private Integer velocidadCarrusel = 5000;

    public EventoConfig() {
    }

    public EventoConfig(Long id, Boolean autoAprobacion, Integer velocidadCarrusel) {
        this.id = id;
        this.autoAprobacion = autoAprobacion;
        this.velocidadCarrusel = velocidadCarrusel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAutoAprobacion() {
        return autoAprobacion;
    }

    public void setAutoAprobacion(Boolean autoAprobacion) {
        this.autoAprobacion = autoAprobacion;
    }

    public Integer getVelocidadCarrusel() {
        return velocidadCarrusel;
    }

    public void setVelocidadCarrusel(Integer velocidadCarrusel) {
        this.velocidadCarrusel = velocidadCarrusel;
    }
}
