package com.novanest.projetooficina.controller;


import com.novanest.projetooficina.entity.OrdemServico;
import com.novanest.projetooficina.enums.StatusOS;
import com.novanest.projetooficina.service.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ordem-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService service;


    @PostMapping
    public ResponseEntity<OrdemServico> criarOrdemServico(OrdemServico ordemServico) {
        service.criarOrdemServico(ordemServico);
        return ResponseEntity.ok(ordemServico);
    }

    @GetMapping("/listar")
    public ResponseEntity<OrdemServico> listarTodasOs() {
        service.listarTodasOs();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscar-por-id")
    public ResponseEntity<OrdemServico> buscarPorId(UUID id) {
        OrdemServico ordemServico = service.buscarPorId(id);
        return ResponseEntity.ok(ordemServico);
    }


    @GetMapping("/buscar-por-cliente-id")
    public ResponseEntity<OrdemServico> buscarPorClienteId(UUID id) {
        service.buscarPorClienteId(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/buscar-por-veiculo-id")
    public ResponseEntity<OrdemServico> buscarPorVeiculoId(UUID id) {
        service.buscarPorVeiculoId(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/buscar-por-numero-os")
    public ResponseEntity<OrdemServico> buscarPorNumeroOs(String numeroOs) {
        service.buscarPorNumeroOs(numeroOs);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/buscar-por-status")
    public ResponseEntity<OrdemServico> buscarPorStatus(StatusOS status) {
        service.buscarPorStatus(status);
        return ResponseEntity.ok().build();
    }


    @PutMapping
    public ResponseEntity<OrdemServico> atualizarOrdemServico(UUID id, OrdemServico ordemServico) {
        service.atualizarOrdemServico(id, ordemServico);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/finalizar")
    public ResponseEntity<OrdemServico> finalizarOrdemServico(UUID id) {
        service.finalizarOrdemServico(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/cancelar")
    public ResponseEntity<OrdemServico> cancelarOrdemServico(UUID id) {
        service.cancelarOrdemServico(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/contar")
    public ResponseEntity<Long> contarOrdensServico() {
        long count = service.contarOrdensServico();
        return ResponseEntity.ok(count);
    }

}
