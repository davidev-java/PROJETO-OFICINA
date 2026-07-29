package com.novanest.projetooficina.demo;

import com.novanest.projetooficina.dto.cliente.ClienteRequestDTO;
import com.novanest.projetooficina.dto.cliente.ClienteResponseDTO;
import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoRequestDTO;
import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoResponseDTO;
import com.novanest.projetooficina.dto.veiculo.VeiculoRequestDTO;
import com.novanest.projetooficina.dto.veiculo.VeiculoResponseDTO;
import com.novanest.projetooficina.enums.StatusOS;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// Modo demo publico: leitura liberada pra qualquer um (é a vitrine do
// portfólio), escrita só pra quem estiver logado como ADMIN (o admin_demo).
// Tudo aqui opera exclusivamente em cima de dados marcados demo=true -
// ver DemoService. Nunca expõe nem altera dado real de produção.
@RestController
@RequiredArgsConstructor
@RequestMapping("/demo")
public class DemoController {

    private final DemoService service;

    @GetMapping("/clientes")
    public List<ClienteResponseDTO> listarClientes() {
        return service.listarClientes();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/clientes")
    public ResponseEntity<ClienteResponseDTO> criarCliente(@RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarCliente(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/clientes/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable UUID id, @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(service.atualizarCliente(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable UUID id) {
        service.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/veiculos")
    public List<VeiculoResponseDTO> listarVeiculos() {
        return service.listarVeiculos();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/veiculos")
    public ResponseEntity<VeiculoResponseDTO> criarVeiculo(@RequestBody VeiculoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarVeiculo(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/veiculos/{id}")
    public ResponseEntity<Void> deletarVeiculo(@PathVariable UUID id) {
        service.deletarVeiculo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ordem-servico")
    public List<OrdemServicoResponseDTO> listarOrdensServico() {
        return service.listarOrdensServico();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ordem-servico")
    public ResponseEntity<OrdemServicoResponseDTO> criarOrdemServico(@RequestBody OrdemServicoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarOrdemServico(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/ordem-servico/{id}/status")
    public ResponseEntity<OrdemServicoResponseDTO> atualizarStatus(@PathVariable UUID id, @RequestParam StatusOS status) {
        return ResponseEntity.ok(service.atualizarStatus(id, status));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/ordem-servico/{id}")
    public ResponseEntity<Void> deletarOrdemServico(@PathVariable UUID id) {
        service.deletarOrdemServico(id);
        return ResponseEntity.noContent().build();
    }
}
