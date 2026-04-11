package com.InfoEvento.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sectores")
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(name = "nombre_display", nullable = false)
    private String nombreDisplay;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("sector")
    private List<Imagen> imagenes;
}
