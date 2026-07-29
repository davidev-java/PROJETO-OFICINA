package com.novanest.projetooficina.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Separado do SecurityConfig de proposito: SecurityConfig depende (via
// CustomOAuth2UserService) do UsuarioService, que agora depende de
// PasswordEncoder. Se esse bean vivesse dentro do SecurityConfig, viraria
// uma dependencia circular na inicializacao do Spring.
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
