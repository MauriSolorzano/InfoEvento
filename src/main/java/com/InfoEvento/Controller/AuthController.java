package com.InfoEvento.Controller;

import com.InfoEvento.DTO.AuthRequestDTO;
import com.InfoEvento.DTO.AuthResponseDTO;
import com.InfoEvento.Entity.Usuario;
import com.InfoEvento.Repository.UsuarioRepository;
import com.InfoEvento.Utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtil;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtil,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        try {
            System.out.println("DATO RECIBIDO: User=" + request.getUsername() + " | Pass=" + request.getPassword());
            System.out.println("HASH CORRECTO PARA 1234: " + new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("1234"));
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            Usuario usuario = usuarioRepository
                    .findByUsernameAndActivoTrue(auth.getName())
                    .orElseThrow();

            String token = jwtUtil.createToken(auth);

            return ResponseEntity.ok(new AuthResponseDTO(
                    token,
                    usuario.getUsername(),
                    usuario.getRol()
            ));

        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }
    }
}
