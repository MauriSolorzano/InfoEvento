package com.InfoEvento.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "imagenes")
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    @JsonIgnoreProperties({"imagenes", "planta", "sectores"})
    private Sector sector;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "url_publica")
    private String urlPublica;

    @Column(nullable = false)
    private Integer orden = 0;

    @Column(name = "subida_en")
    private LocalDateTime subidaEn;

    // false = pendiente de moderación, true = visible en pantalla
    @Column(nullable = false)
    private Boolean aprobada = false;

    // null si fue subida por el público (QR)
    @Column(name = "subida_por")
    private String subidaPor;
}
