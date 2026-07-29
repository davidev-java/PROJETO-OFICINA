package com.novanest.projetooficina.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // Prefixos que um usuario do modo demo pode acessar. Fora disso, mesmo com
    // role ADMIN (caso do admin_demo), o acesso e negado - garante que o modo
    // demo nunca alcance dados/endpoints reais (clientes, veiculos, OS, usuarios).
    private static final List<String> PREFIXOS_LIBERADOS_PARA_DEMO = List.of(
            "/demo", "/auth", "/usuarios/me", "/health", "/oauth2", "/login"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.tokenValido(token)) {
                boolean demo = jwtService.extrairDemo(token);

                if (demo && !rotaLiberadaParaDemo(request.getRequestURI())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"erro\":\"Usuário de demonstração não tem acesso a esse recurso\"}");
                    return;
                }

                String email = jwtService.extrairEmail(token);
                String role = jwtService.extrairRole(token);

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean rotaLiberadaParaDemo(String uri) {
        return PREFIXOS_LIBERADOS_PARA_DEMO.stream().anyMatch(uri::startsWith);
    }
}