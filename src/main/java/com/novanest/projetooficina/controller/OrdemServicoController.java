package com.novanest.projetooficina.controller;


import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoRequestDTO;
import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoResponseDTO;
import com.novanest.projetooficina.enums.StatusOS;
import com.novanest.projetooficina.service.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ordem-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService service;

    // MINHAS OS (role CLIENTE - so as proprias, nao mexe na matriz de permissao do staff)
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/minhas")
    public List<OrdemServicoResponseDTO> minhasOrdens(Authentication authentication) {
        return service.buscarMinhasOrdens(authentication.getName());
    }

    // CREATE (atendente abre a OS)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> criarOrdemServico(@RequestBody OrdemServicoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criarOrdemServico(dto));
    }

    // LIST ALL OS
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping
    public List<OrdemServicoResponseDTO> listarTodasOs() {
        return service.listarTodasOs();
    }

    // LIST PAGINADO (?page=0&size=20) - endpoint novo, nao substitui o de cima
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/paginado")
    public Page<OrdemServicoResponseDTO> listarPaginado(Pageable pageable) {
        return service.listarPaginado(pageable);
    }

    // GET BY ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET BY CLIENT ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/cliente/{clienteId}")
    public List<OrdemServicoResponseDTO> buscarPorClienteId(@PathVariable UUID clienteId) {
        return service.buscarPorClienteId(clienteId);
    }

    // GET BY VEICULO ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/veiculo/{veiculoId}")
    public List<OrdemServicoResponseDTO> buscarPorVeiculoId(@PathVariable UUID veiculoId) {
        return service.buscarPorVeiculoId(veiculoId);
    }

    // GET BY NUMERO OS
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/numero/{numeroOs}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorNumeroOs(@PathVariable String numeroOs) {
        return ResponseEntity.ok(service.buscarPorNumeroOs(numeroOs));
    }

    // GET BY STATUS OS
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/status/{status}")
    public List<OrdemServicoResponseDTO> buscarPorStatus(@PathVariable StatusOS status) {
        return service.buscarPorStatus(status);
    }

    // UPDATE (atendente ajusta dados, mecânico atualiza status/valores do serviço)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> atualizarOrdemServico(
            @PathVariable UUID id,
            @RequestBody OrdemServicoRequestDTO dto) {

        return ResponseEntity.ok(service.atualizarOrdemServico(id, dto));
    }

    // FINALIZAR OS
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'MECANICO')")
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoResponseDTO> finalizarOrdemServico(@PathVariable UUID id) {
        return ResponseEntity.ok(service.finalizarOrdemServico(id));
    }

    // CANCELAR OS
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'MECANICO')")
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdemServicoResponseDTO> cancelarOrdemServico(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelarOrdemServico(id));
    }

    // DELETE
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOrdemServico(@PathVariable UUID id) {
        service.deletarOrdemServico(id);
        return ResponseEntity.noContent().build();
    }

    // CONTAR OS
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @GetMapping("/contar")
    public ResponseEntity<Long> contarOrdensServico() {
        return ResponseEntity.ok(service.contarOrdensServico());
    }

}