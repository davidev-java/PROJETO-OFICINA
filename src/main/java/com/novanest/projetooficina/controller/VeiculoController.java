package com.novanest.projetooficina.controller;

import com.novanest.projetooficina.dto.veiculo.VeiculoRequestDTO;
import com.novanest.projetooficina.dto.veiculo.VeiculoResponseDTO;
import com.novanest.projetooficina.entity.Veiculo;
import com.novanest.projetooficina.repository.VeiculoRepository;
import com.novanest.projetooficina.service.VeiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService service;


    // CREATE
    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> salvarVeiculo(@RequestBody VeiculoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criarVeiculo(dto));
    }


    // LIST ALL
    @GetMapping
    public List<VeiculoResponseDTO> listarVeiculos() {
        return service.listarTodos();
    }


    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorId(@PathVariable UUID id) {
        return  ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET BY MARCA
    @GetMapping("/marca/{marca}")
    public List<VeiculoResponseDTO> buscarVeiculoPorMarca(@PathVariable String marca) {
        return service.buscarPorMarca(marca);
    }


    // GET BY MODELO
    @GetMapping("/modelo/{modelo}")
    public List<VeiculoResponseDTO> buscarVeiculoPorModelo(@PathVariable String modelo) {
        return service.buscarPorModelo(modelo);
    }

    // GET BY PLACA
    @GetMapping("/placa/{placa}")
    public VeiculoResponseDTO buscarPorPlaca(@PathVariable String placa) {
        return service.buscarPorPlaca(placa);
    }

    // GET BY CLIENTE ID
    @GetMapping("/cliente/{clienteId}")
    public List<VeiculoResponseDTO> buscarPorClienteId(@PathVariable UUID clienteId) {
        return service.buscarPorCliente(clienteId);
    }

    // UPDATE VEICULO
    @PutMapping("/{id}")
    public VeiculoResponseDTO atualizarCliente(@PathVariable UUID id, @RequestBody VeiculoRequestDTO veiculo) {
        return service.atualizarVeiculo(id, veiculo);
    }


    // DELETE VEICULO
    @DeleteMapping("/{id}")
    public void excluirVeiculo(@PathVariable UUID id) {
        service.deletarVeiculo(id);
    }



}
