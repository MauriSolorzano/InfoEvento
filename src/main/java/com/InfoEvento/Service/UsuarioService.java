package com.InfoEvento.Service;

import com.InfoEvento.Entity.Sector;
import com.InfoEvento.Entity.Usuario;
import com.InfoEvento.Repository.SectorRepository;
import com.InfoEvento.Repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final SectorRepository sectorRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          SectorRepository sectorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.sectorRepository = sectorRepository;
    }

    public List<Sector> misSectores(String username) {
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Si el visor tiene sectores asignados, devuelve los suyos
        // Si no tiene ninguno asignado, devuelve todos los activos
        if (usuario.getSectores() != null && !usuario.getSectores().isEmpty()) {
            return usuario.getSectores();
        }
        return sectorRepository.findByActivoTrue();
    }
}
