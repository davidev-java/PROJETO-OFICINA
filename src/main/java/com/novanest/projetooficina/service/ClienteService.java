package com.novanest.projetooficina.service;

import com.novanest.projetooficina.dto.cliente.ClienteRequestDTO;
import com.novanest.projetooficina.dto.cliente.ClienteResponseDTO;
import com.novanest.projetooficina.entity.Cliente;
import com.novanest.projetooficina.exception.ClienteNaoEncontradoException;
import com.novanest.projetooficina.mapper.ClienteMapper;
import com.novanest.projetooficina.repository.ClienteRepository;
import com.novanest.projetooficina.validate.ClienteValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

        private final ClienteRepository clienteRepository;
        private final ClienteValidate clienteValidate;
        private final ClienteMapper clienteMapper;

    // =========================
    // CRIAR CLIENTE
    // =========================
    public ClienteResponseDTO criarCliente(ClienteRequestDTO dto) {

        Cliente cliente = clienteMapper.toEntity(dto);

        clienteValidate.validarCliente(cliente);

        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        if (cliente.getCpf() != null &&
                clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (cliente.getCnpj() != null && clienteRepository.existsByCnpj(cliente.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado");
        }

        Cliente salvo = clienteRepository.save(cliente);

        return clienteMapper.toDTO(salvo);
    }

    // =========================
    // LISTAR TODOS OS CLIENTES
    // =========================
    public List<ClienteResponseDTO> listarTodos() {

        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toDTO)
                .toList();
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    public ClienteResponseDTO buscarPorId(UUID id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));

        return clienteMapper.toDTO(cliente);
    }


    // =========================
    // BUSCAR POR ID ENTITY
    // =========================
    public Cliente buscarPorIdEntity(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));
    }


    // =========================
    // BUSCAR POR CPF
    // =========================
    public ClienteResponseDTO buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteNaoEncontradoException("CPF não encontrado"));

        return clienteMapper.toDTO(cliente);
    }

    // =========================
    // BUSCAR POR NOME IGNORANDO PARTES INCOMPLETAS
    // =========================
    public List<ClienteResponseDTO> buscarPorNome(String nome) {

        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);

        return clientes.stream()
                .map(clienteMapper::toDTO)
                .toList();
    }

    // =========================
    // ATUALIZAR CLIENTE
    // =========================
    public ClienteResponseDTO atualizarCliente(UUID id, ClienteRequestDTO dados) {

        Cliente cliente = buscarPorIdEntity(id);

        if (dados.getEmail() != null && !dados.getEmail().equals(cliente.getEmail())) {
            if (clienteRepository.existsByEmail(dados.getEmail())) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
            cliente.setEmail(dados.getEmail());
        }

        if (dados.getCpf() != null && !dados.getCpf().equals(cliente.getCpf())) {
            if (clienteRepository.existsByCpf(dados.getCpf())) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }
            cliente.setCpf(dados.getCpf());
        }

        if (dados.getNome() != null) cliente.setNome(dados.getNome());
        if (dados.getTelefone() != null) cliente.setTelefone(dados.getTelefone());
        if (dados.getEndereco() != null) cliente.setEndereco(dados.getEndereco());
        if (dados.getCidade() != null) cliente.setCidade(dados.getCidade());
        if (dados.getEstado() != null) cliente.setEstado(dados.getEstado());
        if (dados.getWhatsapp() != null) cliente.setWhatsapp(dados.getWhatsapp());
        if (dados.getObservacoes() != null) cliente.setObservacoes(dados.getObservacoes());

        Cliente atualizado = clienteRepository.save(cliente);

        return clienteMapper.toDTO(atualizado);
    }

    // =========================
    // DELETAR CLIENTE
    // =========================
    public void deletarCliente(UUID id) {
        Cliente cliente = buscarPorIdEntity(id);
        clienteRepository.delete(cliente);
    }

}
