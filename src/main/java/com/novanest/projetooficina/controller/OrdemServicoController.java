package com.novanest.projetooficina.controller;


import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoRequestDTO;
import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoResponseDTO;
import com.novanest.projetooficina.entity.OrdemServico;
import com.novanest.projetooficina.enums.StatusOS;
import com.novanest.projetooficina.service.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ordem-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService service;

    // CREATE
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> criarOrdemServico(OrdemServicoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criarOrdemServico(dto));
    }


    // LIST ALL OS
    @GetMapping("/listar")
    public ResponseEntity<OrdemServicoResponseDTO> listarTodasOs() {
        service.listarTodasOs();
        return ResponseEntity.ok().build();
    }


    // GET BY ID
    @GetMapping("/buscar-por-id")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }


    // GET BY CLIENT ID
    @GetMapping("/buscar-por-cliente-id")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorClienteId(UUID id) {
        service.buscarPorClienteId(id);
        return ResponseEntity.ok().build();
    }


    // GET BY VEICULO ID
    @GetMapping("/buscar-por-veiculo-id")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorVeiculoId(UUID id) {
        service.buscarPorVeiculoId(id);
        return ResponseEntity.ok().build();
    }


    // GET BY NUMERO OS
    @GetMapping("/buscar-por-numero-os")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorNumeroOs(String numeroOs) {
        service.buscarPorNumeroOs(numeroOs);
        return ResponseEntity.ok().build();
    }


    // GET BY STATUS OS
    @GetMapping("/buscar-por-status")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorStatus(StatusOS status) {
        service.buscarPorStatus(status);
        return ResponseEntity.ok().build();
    }


    // UPDATE
    @PutMapping
    public ResponseEntity<OrdemServicoResponseDTO> atualizarOrdemServico(UUID id, OrdemServicoRequestDTO ordemServico) {
        service.atualizarOrdemServico(id, ordemServico);
        return ResponseEntity.ok().build();
    }


    // FINALIZAR OS
    @PostMapping("/finalizar")
    public ResponseEntity<OrdemServico> finalizarOrdemServico(UUID id) {
        service.finalizarOrdemServico(id);
        return ResponseEntity.ok().build();
    }


    // CANCELAR OS
    @PostMapping("/cancelar")
    public ResponseEntity<OrdemServico> cancelarOrdemServico(UUID id) {
        service.cancelarOrdemServico(id);
        return ResponseEntity.ok().build();
    }

    // CANCELAR OS
    @DeleteMapping("/deletar")
    public ResponseEntity<OrdemServico> deletarOrdemServico(UUID id) {
        service.deletarOrdemServico(id);
        return ResponseEntity.ok().build();
    }


    // CONTAR OS
    @GetMapping("/contar")
    public ResponseEntity<Long> contarOrdensServico() {
        long count = service.contarOrdensServico();
        return ResponseEntity.ok(count);
    }

}
