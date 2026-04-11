package com.InfoEvento.Service;

import com.InfoEvento.Entity.EventoConfig;
import com.InfoEvento.Entity.Sector;
import com.InfoEvento.Entity.Usuario;
import com.InfoEvento.Repository.EventoConfigRepository;
import com.InfoEvento.Repository.SectorRepository;
import com.InfoEvento.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final SectorRepository sectorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventoConfigRepository eventoConfigRepository;

    public AdminService(SectorRepository sectorRepository,
                        UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        EventoConfigRepository eventoConfigRepository) {
        this.sectorRepository = sectorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventoConfigRepository = eventoConfigRepository;
    }

    // ─── SECTORES ─────────────────────────────────────────────
    public Sector crearSector(String nombre, String nombreDisplay) {
        if (sectorRepository.findByNombre(nombre.toUpperCase()).isPresent())
            throw new IllegalArgumentException("Ya existe un sector con ese nombre");

        Sector sector = new Sector();
        sector.setNombre(nombre.toUpperCase());
        sector.setNombreDisplay(nombreDisplay);
        sector.setActivo(true);
        return sectorRepository.save(sector);
    }

    public Sector actualizarSector(Long id, Map<String, Object> body) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado"));

        if (body.containsKey("nombreDisplay"))
            sector.setNombreDisplay((String) body.get("nombreDisplay"));
        if (body.containsKey("activo"))
            sector.setActivo((Boolean) body.get("activo"));

        return sectorRepository.save(sector);
    }

    public void eliminarSector(Long id) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado"));
        sectorRepository.delete(sector);
    }

    public List<Sector> listarSectores() {
        return sectorRepository.findByActivoTrue();
    }

    // ─── AUTO-APROBACIÓN GLOBAL ───────────────────────────────
    public boolean getAutoAprobacion() {
        return eventoConfigRepository.findById(1L)
                .map(EventoConfig::getAutoAprobacion)
                .orElse(false);
    }

    public void setAutoAprobacion(boolean valor) {
        EventoConfig config = eventoConfigRepository.findById(1L)
                .orElse(new EventoConfig()); // si no existe, la crea
        config.setAutoAprobacion(valor);
        eventoConfigRepository.save(config);
    }

    // ─── USUARIOS ─────────────────────────────────────────────
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @SuppressWarnings("unchecked")
    public Usuario crearUsuario(Map<String, Object> body) {
        String username = (String) body.get("username");
        if (usuarioRepository.findByUsernameAndActivoTrue(username).isPresent())
            throw new IllegalArgumentException("El usuario ya existe");

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode((String) body.get("password")));
        usuario.setRol((String) body.get("rol"));
        usuario.setActivo(true);


        if (body.containsKey("sectorIds") && body.get("sectorIds") != null) {
            List<Integer> sectorIds = (List<Integer>) body.get("sectorIds");
            List<Sector> sectores = sectorIds.stream()
                    .map(sId -> sectorRepository.findById(Long.valueOf(sId)).orElseThrow())
                    .toList();
            usuario.setSectores(sectores);
        }

        return usuarioRepository.save(usuario);
    }

    @SuppressWarnings("unchecked")
    public Usuario actualizarUsuario(Long id, Map<String, Object> body) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (body.containsKey("password") && body.get("password") != null)
            usuario.setPasswordHash(passwordEncoder.encode((String) body.get("password")));
        if (body.containsKey("activo"))
            usuario.setActivo((Boolean) body.get("activo"));
        if (body.containsKey("rol"))
            usuario.setRol((String) body.get("rol"));


        if (body.containsKey("sectorIds") && body.get("sectorIds") != null) {
            List<Integer> sectorIds = (List<Integer>) body.get("sectorIds");
            List<Sector> sectores = sectorIds.stream()
                    .map(sId -> sectorRepository.findById(Long.valueOf(sId)).orElseThrow())
                    .toList();
            usuario.setSectores(sectores);
        }

        return usuarioRepository.save(usuario);
    }

    // Configuracion Carruse
    public int getVelocidadCarrusel() {
        return eventoConfigRepository.findById(1L)
                .map(EventoConfig::getVelocidadCarrusel)
                .orElse(5000);
    }

    public void setVelocidadCarrusel(int velocidad) {
        EventoConfig config = eventoConfigRepository.findById(1L)
                .orElse(new EventoConfig());
        config.setVelocidadCarrusel(velocidad);
        eventoConfigRepository.save(config);
    }
}
