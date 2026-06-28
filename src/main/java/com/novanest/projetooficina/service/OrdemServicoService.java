package com.novanest.projetooficina.service;


import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoRequestDTO;
import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoResponseDTO;
import com.novanest.projetooficina.entity.OrdemServico;
import com.novanest.projetooficina.enums.StatusOS;
import com.novanest.projetooficina.exception.OrdemServicoNaoEncontradaException;
import com.novanest.projetooficina.mapper.OrdemServicoMapper;
import com.novanest.projetooficina.repository.OrdemServicoRepository;
import com.novanest.projetooficina.validate.OrdemServicoValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final OrdemServicoValidate validar;
    private final OrdemServicoMapper mapper;


    // =========================
    // CRIAR ORDEM DE SERVICO
    // =========================
    public OrdemServicoResponseDTO criarOrdemServico(OrdemServicoRequestDTO dto) {
        OrdemServico ordemServico = mapper.toEntity(dto);

        validar.validarOrdemServico(ordemServico);
        ordemServico.setValorTotal(calcularValorTotal(ordemServico));
        OrdemServico salvo = repository.save(ordemServico);
        return mapper.toDTO(salvo);
    }


    // =========================
    // LISTAR TODAS AS OS
    // =========================
    public List<OrdemServicoResponseDTO> listarTodasOs() {

        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }


    // =========================
    // BUSCAR POR ID
    // =========================
    public OrdemServicoResponseDTO buscarPorId(UUID id) {
        OrdemServico ordemServico = repository.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada!"));

        return mapper.toDTO(ordemServico);
    }


    // =========================
    // BUSCAR POR ID ENTITY
    // =========================
    public OrdemServico buscarPorIdEntity(UUID id) {
       return repository.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada!"));


    }

    // =========================
    // BUSCAR POR CLIENTE ID
    // =========================
    public List<OrdemServicoResponseDTO> buscarPorClienteId(UUID id){
        List<OrdemServico> ordemServicoList = repository.findByClienteId(id);
        validarLista(ordemServicoList, "Nenhuma ordem de serviço encontrada para este cliente por cliente-id");
        return ordemServicoList
                .stream()
                .map(mapper::toDTO)
                .toList();
    }


    // =========================
    // BUSCAR POR VEICULO ID
    // =========================
    public List<OrdemServicoResponseDTO> buscarPorVeiculoId(UUID id){
        List<OrdemServico> ordemServicoList = repository.findByVeiculoId(id);
        validarLista(ordemServicoList, "Nenhuma ordem de serviço encontrada para este veículo.");
        return ordemServicoList
                .stream()
                .map(mapper::toDTO)
                .toList();
    }


    // =========================
    // BUSCAR POR NUMERO OS
    // =========================
    public OrdemServicoResponseDTO buscarPorNumeroOs(String numeroOs) {
        OrdemServico ordemServico = repository.findByNumeroOs(numeroOs)
                .orElseThrow(() ->
                        new OrdemServicoNaoEncontradaException(
                                "Nenhuma ordem de serviço encontrada para o número informado."));

        return mapper.toDTO(ordemServico);
    }


    // =========================
    // BUSCAR POR STATUS
    // =========================
    public List<OrdemServicoResponseDTO> buscarPorStatus(StatusOS status){
        List<OrdemServico> ordemServicoList = repository.findByStatus(status);
        validarLista(ordemServicoList, "Nenhuma ordem de serviço encontrada para o status informado.");
        return ordemServicoList
                .stream()
                .map(mapper::toDTO)
                .toList();
    }


    // =========================
    // ATUALIZAR ORDEM DE SERVICO
    // =========================
    public OrdemServicoResponseDTO atualizarOrdemServico(UUID id, OrdemServicoRequestDTO dados) {
        OrdemServico os = buscarPorIdEntity(id);

        os.setDescricaoProblema(dados.getDescricaoProblema());
        os.setValorMaoDeObra(dados.getValorMaoDeObra());
        os.setValorPecas(dados.getValorPecas());
        os.setDesconto(dados.getDesconto());
        os.setStatus(dados.getStatusOS());

        os.setValorTotal(calcularValorTotal(os));

        validar.validarOrdemServico(os);

        OrdemServico salvo = repository.save(os);
        return mapper.toDTO(salvo);
    }


    // =========================
    // FINALIZAR ORDEM DE SERVICO
    // =========================
    public OrdemServicoResponseDTO finalizarOrdemServico(UUID id) {
        OrdemServico os = buscarPorIdEntity(id);

        os.setStatus(StatusOS.FINALIZADA);
        os.setDataFechamento(LocalDate.now());

        OrdemServico finalizada = repository.save(os);
        return mapper.toDTO(finalizada);
    }


    // =========================
    // CANCELAR ORDEM DE SERVICO
    // =========================
    public OrdemServicoResponseDTO cancelarOrdemServico(UUID id) {
        OrdemServico os = buscarPorIdEntity(id);

        os.setStatus(StatusOS.CANCELADA);

        OrdemServico cancelar = repository.save(os);
        return mapper.toDTO(cancelar);
    }


    // =========================
    // DELETAR ORDEM DE SERVICO
    // =========================
    public void deletarOrdemServico(UUID id) {
        OrdemServico os = buscarPorIdEntity(id);
        repository.delete(os);
    }


    // =========================
    // CONTAR ORDEM DE SERVICO
    // =========================
    public long contarOrdensServico() {
        return repository.count();
    }


    // =========================
    // VALIDAR LISTA
    // =========================
    private <T> void validarLista(List<T> lista, String mensagem) {
        if (lista.isEmpty()) {
            throw new OrdemServicoNaoEncontradaException(mensagem);
        }
    }

    // =========================
    // CALCULAR TOTAL
    // =========================
    private BigDecimal calcularValorTotal(OrdemServico os) {
        return os.getValorPecas()
                .add(os.getValorMaoDeObra())
                .subtract(os.getDesconto());
    }

}
