package com.InfoEvento.Config;

import com.InfoEvento.Config.Filter.JwtTokenValidator;
import com.InfoEvento.Service.UserDetailsServiceImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig {

    private final JwtTokenValidator jwtFilter;
    private final UserDetailsServiceImp userDetailsService;

    public SecurityConfig(JwtTokenValidator jwtFilter, UserDetailsServiceImp userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // WebSocket sin cambios
                    auth.requestMatchers("/ws/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/admin/publico/sectores").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/admin/publico/sectores").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/admin/publico/velocidad-carrusel").permitAll();

                    // Solo el superadmin accede a todo /api/admin/** (sectores, usuarios,
                    // configuracion, moderacion)
                    auth.requestMatchers("/api/admin/**").hasRole("SUPERADMIN");

                    // Público: login
                    auth.requestMatchers("/api/auth").permitAll();

                    // ✅ Público: subida por QR (la gente escanea y sube sin loguearse)
                    auth.requestMatchers(HttpMethod.POST, "/api/imagenes/publico").permitAll();

                    // ✅ Público: el televisor consume este endpoint sin JWT
                    auth.requestMatchers(HttpMethod.GET, "/api/imagenes/*/aprobadas").permitAll();

                    // Recursos estáticos si los seguís sirviendo desde Spring
                    auth.requestMatchers("/login.html", "/login.css", "/img/**").permitAll();

                    // Todo lo demás requiere autenticación (visor, admin de imágenes, etc.)
                    auth.anyRequest().authenticated();
                })

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "http://localhost:3000",
                "http://2.24.30.153:3000",
                "https://infoevento.duckdns.org",
                "http://infoevento.duckdns.org"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type",
                "X-Requested-With", "Accept"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
