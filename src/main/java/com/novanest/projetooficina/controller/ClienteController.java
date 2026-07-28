package com.novanest.projetooficina.controller;


import com.novanest.projetooficina.dto.cliente.ClienteRequestDTO;
import com.novanest.projetooficina.dto.cliente.ClienteResponseDTO;
import com.novanest.projetooficina.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    // CREATE (recepção cadastra o cliente)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> salvarCliente(@RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criarCliente(dto));
    }

    // UPDATE
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(
            @PathVariable UUID id,
            @RequestBody ClienteRequestDTO dto) {

        return ResponseEntity.ok(service.atualizarCliente(id, dto));
    }

    // DELETE
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable UUID id) {
        service.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }

    // LIST ALL
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping
    public List<ClienteResponseDTO> listarClientes() {
        return service.listarTodos();
    }

    // GET BY ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET BY CPF
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(service.buscarPorCpf(cpf));
    }

    // GET BY NAME
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/nome")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }
}