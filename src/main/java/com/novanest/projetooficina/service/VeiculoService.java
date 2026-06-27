package com.novanest.projetooficina.service;


import com.novanest.projetooficina.dto.veiculo.VeiculoRequestDTO;
import com.novanest.projetooficina.dto.veiculo.VeiculoResponseDTO;
import com.novanest.projetooficina.entity.Cliente;
import com.novanest.projetooficina.entity.Veiculo;
import com.novanest.projetooficina.exception.VeiculoNaoEncontradoException;
import com.novanest.projetooficina.mapper.VeiculoMapper;
import com.novanest.projetooficina.repository.VeiculoRepository;
import com.novanest.projetooficina.validate.VeiculoValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeiculoService {


    private final VeiculoRepository veiculoRepository;
    private final VeiculoValidate veiculoValidate;
    private final VeiculoMapper veiculoMapper;
    private final ClienteService clienteService;

    // =========================
    // CRIAR VEICULO
    // =========================
    public VeiculoResponseDTO criarVeiculo(VeiculoRequestDTO dto) {

        Veiculo veiculo = veiculoMapper.toEntity(dto);

        Cliente cliente = clienteService.buscarPorIdEntity(dto.getClienteId());

        veiculo.setCliente(cliente);

        veiculoValidate.validarVeiculo(veiculo);

        if (veiculoRepository.existsByPlaca(veiculo.getPlaca())) {
            throw new IllegalArgumentException("Placa já cadastrada!");
        }

        Veiculo salvo = veiculoRepository.save(veiculo);

        return veiculoMapper.toDTO(salvo);
    }

    // =========================
    // LISTAR TODOS OS VEICULOS
    // =========================
    public List<VeiculoResponseDTO> listarTodos() {
        return veiculoRepository.findAll()
                .stream()
                .map(veiculoMapper::toDTO)
                .toList();
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    public VeiculoResponseDTO buscarPorId(UUID id) {
        Veiculo vceiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo não encontrado!"));

        return veiculoMapper.toDTO(vceiculo);
    }


    // =========================
    // BUSCAR POR ID ENTITY
    // =========================
    public Veiculo buscarPorIdEntity(UUID id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo não encontrado!"));
    }

    // =========================
    // BUSCAR POR MARCA
    // =========================
    public List<VeiculoResponseDTO> buscarPorMarca(String marca) {
        List<Veiculo> veiculos = veiculoRepository.findByMarcaContainingIgnoreCase(marca);
        validarLista(veiculos, "Nenhum veículo encontrado para essa marca");
        return veiculos.stream()
                .map(veiculoMapper::toDTO)
                .toList();
    }

    // =========================
    // BUSCAR POR MODELO
    // =========================
    public List<VeiculoResponseDTO> buscarPorModelo(String modelo) {
        List<Veiculo> veiculos = veiculoRepository.findByModeloContainingIgnoreCase(modelo);
        validarLista(veiculos, "Nenhum veículo encontrado para esse modelo");
        return veiculos.stream()
                .map(veiculoMapper::toDTO)
                .toList();
    }

    // =========================
    // BUSCAR POR PLACA
    // =========================
    public VeiculoResponseDTO buscarPorPlaca(String placa) {
        Veiculo veiculo = veiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo não encontrado!"));

        return veiculoMapper.toDTO(veiculo);
    }

    // =========================
    // BUSCAR POR CLIENTE
    // =========================
    public List<VeiculoResponseDTO> buscarPorCliente(UUID clienteId) {
        List<Veiculo> veiculos =
                veiculoRepository.findByCliente_id(clienteId);

        validarLista(veiculos,
                "Nenhum veículo encontrado para este cliente");

        return veiculos.stream()
                .map(veiculoMapper::toDTO)
                .toList();
    }

    // =========================
    // ATUALIZAR VEICULO
    // =========================
    public VeiculoResponseDTO atualizarVeiculo(UUID id, VeiculoRequestDTO dados) {

        Veiculo veiculo = buscarPorIdEntity(id);


        if (dados.getPlaca() != null &&
                !dados.getPlaca().equals(veiculo.getPlaca())) {

            if (veiculoRepository.existsByPlaca(dados.getPlaca())) {
                throw new IllegalArgumentException("Placa já cadastrada!");
            }

            veiculo.setPlaca(dados.getPlaca());
        }

        if (dados.getMarca() != null) {
            veiculo.setMarca(dados.getMarca());
        }

        if (dados.getModelo() != null) {
            veiculo.setModelo(dados.getModelo());
        }

        if (dados.getAno() != null) {
            veiculo.setAno(dados.getAno());
        }

        if (dados.getCor() != null) {
            veiculo.setCor(dados.getCor());
        }

        if (dados.getQuilometragem() != null) {
            veiculo.setQuilometragem(dados.getQuilometragem());
        }

        if (dados.getClienteId() != null) {
            Cliente cliente = clienteService.buscarPorIdEntity(dados.getClienteId());
            veiculo.setCliente(cliente);
        }

        veiculoValidate.validarVeiculo(veiculo);

        Veiculo salvo = veiculoRepository.save(veiculo);
        return veiculoMapper.toDTO(salvo);
    }

    // =========================
    // DELETAR VEICULO
    // =========================
    public void deletarVeiculo(UUID id) {
        Veiculo veiculo = buscarPorIdEntity(id);
        veiculoRepository.delete(veiculo);
    }

    // =========================
    // VALIDAR LISTA
    // =========================
    private void validarLista(List<Veiculo> veiculos, String mensagem) {
        if (veiculos.isEmpty()) {
            throw new VeiculoNaoEncontradoException(mensagem);
        }
    }

}
