package com.novanest.projetooficina.controller;

import com.novanest.projetooficina.dto.relatorio.RelatorioResumoDTO;
import com.novanest.projetooficina.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService service;

    // Numeros da operacao real. O relatorio do modo demo mora no DemoController.
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @GetMapping("/resumo")
    public RelatorioResumoDTO resumo() {
        return service.gerarResumo(false);
    }
}
