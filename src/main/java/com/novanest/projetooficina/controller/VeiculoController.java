package com.novanest.projetooficina.controller;

import com.novanest.projetooficina.dto.veiculo.VeiculoRequestDTO;
import com.novanest.projetooficina.dto.veiculo.VeiculoResponseDTO;
import com.novanest.projetooficina.service.VeiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService service;


    // CREATE
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> salvarVeiculo(@RequestBody VeiculoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criarVeiculo(dto));
    }


    // LIST ALL
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping
    public List<VeiculoResponseDTO> listarVeiculos() {
        return service.listarTodos();
    }


    // GET BY ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorId(@PathVariable UUID id) {
        return  ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET BY MARCA
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/marca/{marca}")
    public List<VeiculoResponseDTO> buscarVeiculoPorMarca(@PathVariable String marca) {
        return service.buscarPorMarca(marca);
    }


    // GET BY MODELO
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/modelo/{modelo}")
    public List<VeiculoResponseDTO> buscarVeiculoPorModelo(@PathVariable String modelo) {
        return service.buscarPorModelo(modelo);
    }

    // GET BY PLACA
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/placa/{placa}")
    public VeiculoResponseDTO buscarPorPlaca(@PathVariable String placa) {
        return service.buscarPorPlaca(placa);
    }

    // GET BY CLIENTE ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO')")
    @GetMapping("/cliente/{clienteId}")
    public List<VeiculoResponseDTO> buscarPorClienteId(@PathVariable UUID clienteId) {
        return service.buscarPorCliente(clienteId);
    }

    // UPDATE VEICULO
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    @PutMapping("/{id}")
    public VeiculoResponseDTO atualizarCliente(@PathVariable UUID id, @RequestBody VeiculoRequestDTO veiculo) {
        return service.atualizarVeiculo(id, veiculo);
    }


    // DELETE VEICULO
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @DeleteMapping("/{id}")
    public void excluirVeiculo(@PathVariable UUID id) {
        service.deletarVeiculo(id);
    }



}
