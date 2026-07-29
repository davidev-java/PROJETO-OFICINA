package com.novanest.projetooficina.demo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Limite simples por IP nas escritas do modo demo (POST/PUT/PATCH/DELETE em
// /demo/**), pra evitar abuso ja que esses endpoints sao publicos. Nao e
// distribuido (reseta se a instancia reiniciar), mas resolve o caso real:
// um unico visitante clicando sem parar num app free-tier de portfolio.
@Component
public class DemoRateLimitFilter extends OncePerRequestFilter {

    private static final int LIMITE_POR_JANELA = 30;
    private static final long JANELA_MS = 10 * 60 * 1000; // 10 minutos
    private static final Set<String> METODOS_ESCRITA = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final ConcurrentHashMap<String, Contador> contadores = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        boolean escritaNoDemo = request.getRequestURI().startsWith("/demo")
                && METODOS_ESCRITA.contains(request.getMethod());

        if (escritaNoDemo && limiteExcedido(ipDoCliente(request))) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"erro\":\"Muitas requisições no modo demo, tente novamente em alguns minutos\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean limiteExcedido(String ip) {
        long agora = System.currentTimeMillis();

        Contador contador = contadores.computeIfAbsent(ip, k -> new Contador(agora));

        if (agora - contador.inicioJanela > JANELA_MS) {
            contador.inicioJanela = agora;
            contador.total.set(0);
        }

        return contador.total.incrementAndGet() > LIMITE_POR_JANELA;
    }

    private String ipDoCliente(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        return forwardedFor != null ? forwardedFor.split(",")[0].trim() : request.getRemoteAddr();
    }

    private static class Contador {
        volatile long inicioJanela;
        final AtomicInteger total = new AtomicInteger(0);

        Contador(long inicioJanela) {
            this.inicioJanela = inicioJanela;
        }
    }
}
