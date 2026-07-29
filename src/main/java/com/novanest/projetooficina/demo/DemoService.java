package com.novanest.projetooficina.demo;

import com.novanest.projetooficina.dto.cliente.ClienteRequestDTO;
import com.novanest.projetooficina.dto.cliente.ClienteResponseDTO;
import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoRequestDTO;
import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoResponseDTO;
import com.novanest.projetooficina.dto.veiculo.VeiculoRequestDTO;
import com.novanest.projetooficina.dto.veiculo.VeiculoResponseDTO;
import com.novanest.projetooficina.entity.Cliente;
import com.novanest.projetooficina.entity.OrdemServico;
import com.novanest.projetooficina.entity.Veiculo;
import com.novanest.projetooficina.enums.StatusOS;
import com.novanest.projetooficina.exception.ClienteNaoEncontradoException;
import com.novanest.projetooficina.exception.OrdemServicoNaoEncontradaException;
import com.novanest.projetooficina.exception.VeiculoNaoEncontradoException;
import com.novanest.projetooficina.mapper.ClienteMapper;
import com.novanest.projetooficina.mapper.OrdemServicoMapper;
import com.novanest.projetooficina.mapper.VeiculoMapper;
import com.novanest.projetooficina.repository.ClienteRepository;
import com.novanest.projetooficina.repository.OrdemServicoRepository;
import com.novanest.projetooficina.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Espelha um subconjunto do CRUD real (Cliente/Veiculo/OrdemServico), mas
// SEMPRE restrito a linhas com demo=true - por construcao, nada aqui
// consegue ler ou escrever dado real de producao, nao importa quem chama.
@Service
@RequiredArgsConstructor
public class DemoService {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteMapper clienteMapper;
    private final VeiculoMapper veiculoMapper;
    private final OrdemServicoMapper ordemServicoMapper;

    // ===== CLIENTES =====

    public List<ClienteResponseDTO> listarClientes() {
        return clienteRepository.findByDemoTrue().stream().map(clienteMapper::toDTO).toList();
    }

    public ClienteResponseDTO criarCliente(ClienteRequestDTO dto) {
        Cliente cliente = clienteMapper.toEntity(dto);
        cliente.setDemo(true);
        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    public ClienteResponseDTO atualizarCliente(UUID id, ClienteRequestDTO dto) {
        Cliente cliente = buscarClienteDemo(id);
        if (dto.getNome() != null) cliente.setNome(dto.getNome());
        if (dto.getTelefone() != null) cliente.setTelefone(dto.getTelefone());
        if (dto.getEndereco() != null) cliente.setEndereco(dto.getEndereco());
        if (dto.getCidade() != null) cliente.setCidade(dto.getCidade());
        if (dto.getEstado() != null) cliente.setEstado(dto.getEstado());
        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    public void deletarCliente(UUID id) {
        clienteRepository.delete(buscarClienteDemo(id));
    }

    private Cliente buscarClienteDemo(UUID id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente demo não encontrado"));
        exigirDemo(cliente.isDemo(), "Cliente");
        return cliente;
    }

    // ===== VEICULOS =====

    public List<VeiculoResponseDTO> listarVeiculos() {
        return veiculoRepository.findByDemoTrue().stream().map(veiculoMapper::toDTO).toList();
    }

    public VeiculoResponseDTO criarVeiculo(VeiculoRequestDTO dto) {
        Cliente cliente = buscarClienteDemo(dto.getClienteId());
        Veiculo veiculo = veiculoMapper.toEntity(dto);
        veiculo.setCliente(cliente);
        veiculo.setDemo(true);
        return veiculoMapper.toDTO(veiculoRepository.save(veiculo));
    }

    public void deletarVeiculo(UUID id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo demo não encontrado"));
        exigirDemo(veiculo.isDemo(), "Veículo");
        veiculoRepository.delete(veiculo);
    }

    // ===== ORDENS DE SERVICO =====

    public List<OrdemServicoResponseDTO> listarOrdensServico() {
        return ordemServicoRepository.findByDemoTrue().stream().map(ordemServicoMapper::toDTO).toList();
    }

    public OrdemServicoResponseDTO criarOrdemServico(OrdemServicoRequestDTO dto) {
        Cliente cliente = buscarClienteDemo(dto.getClienteId());
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo demo não encontrado"));
        exigirDemo(veiculo.isDemo(), "Veículo");

        OrdemServico os = ordemServicoMapper.toEntity(dto);
        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        os.setDemo(true);

        BigDecimal total = os.getValorPecas().add(os.getValorMaoDeObra()).subtract(os.getDesconto());
        os.setValorTotal(total);

        return ordemServicoMapper.toDTO(ordemServicoRepository.save(os));
    }

    public OrdemServicoResponseDTO atualizarStatus(UUID id, StatusOS status) {
        OrdemServico os = buscarOrdemServicoDemo(id);
        os.setStatus(status);
        return ordemServicoMapper.toDTO(ordemServicoRepository.save(os));
    }

    public void deletarOrdemServico(UUID id) {
        ordemServicoRepository.delete(buscarOrdemServicoDemo(id));
    }

    private OrdemServico buscarOrdemServicoDemo(UUID id) {
        OrdemServico os = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de serviço demo não encontrada"));
        exigirDemo(os.isDemo(), "Ordem de serviço");
        return os;
    }

    private void exigirDemo(boolean demo, String recurso) {
        if (!demo) {
            throw new IllegalArgumentException(recurso + " não pertence ao modo demo");
        }
    }
}
