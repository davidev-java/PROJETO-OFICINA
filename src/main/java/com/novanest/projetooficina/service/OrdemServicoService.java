package com.novanest.projetooficina.service;


import com.novanest.projetooficina.entity.OrdemServico;
import com.novanest.projetooficina.enums.StatusOS;
import com.novanest.projetooficina.exception.OrdemServicoNaoEncontradaException;
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


    // =========================
    // CRIAR ORDEM DE SERVICO
    // =========================
    public OrdemServico criarOrdemServico(OrdemServico ordemServico) {
        validar.validarOrdemServico(ordemServico);
        ordemServico.setValorTotal(calcularTotal(ordemServico));
        return repository.save(ordemServico);
    }


    // =========================
    // LISTAR TODAS AS OS
    // =========================
    public List<OrdemServico> listarTodasOs() {
        return repository.findAll();
    }


    // =========================
    // BUSCAR POR ID
    // =========================
    public OrdemServico buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada!"));
    }

    // =========================
    // BUSCAR POR CLIENTE ID
    // =========================
    public List<OrdemServico> buscarPorClienteId(UUID id){
        List<OrdemServico> ordemServicoList = repository.findByClienteId(id);
        validarLista(ordemServicoList, "Nenhuma ordem de serviço encontrada para este cliente por cliente-id");
        return ordemServicoList;
    }


    // =========================
    // BUSCAR POR VEICULO ID
    // =========================
    public List<OrdemServico> buscarPorVeiculoId(UUID id){
        List<OrdemServico> ordemServicoList = repository.findByVeiculoId(id);
        validarLista(ordemServicoList, "Nenhuma ordem de serviço encontrada para este veículo.");
        return ordemServicoList;
    }


    // =========================
    // BUSCAR POR NUMERO OS
    // =========================
    public OrdemServico buscarPorNumeroOs(String numeroOs) {
        return repository.findByNumeroOs(numeroOs)
                .orElseThrow(() ->
                        new OrdemServicoNaoEncontradaException(
                                "Nenhuma ordem de serviço encontrada para o número informado."));
    }


    // =========================
    // BUSCAR POR STATUS
    // =========================
    public List<OrdemServico> buscarPorStatus(StatusOS status){
        List<OrdemServico> ordemServicoList = repository.findByStatus(status);
        validarLista(ordemServicoList, "Nenhuma ordem de serviço encontrada para o status informado.");
        return ordemServicoList;
    }


    // =========================
    // ATUALIZAR ORDEM DE SERVICO
    // =========================
    public OrdemServico atualizarOrdemServico(UUID id, OrdemServico dados) {
        OrdemServico os = buscarPorId(id);
        validar.validarOrdemServico(dados);

        os.setDescricaoProblema(dados.getDescricaoProblema());
        os.setValorMaoDeObra(dados.getValorMaoDeObra());
        os.setValorPecas(dados.getValorPecas());
        os.setDesconto(dados.getDesconto());
        os.setStatus(dados.getStatus());

        os.setValorTotal(calcularTotal(os));

        return repository.save(os);
    }


    // =========================
    // FINALIZAR ORDEM DE SERVICO
    // =========================
    public OrdemServico finalizarOrdemServico(UUID id) {
        OrdemServico os = buscarPorId(id);

        os.setStatus(StatusOS.FINALIZADA);
        os.setDataFechamento(LocalDate.now());

        return repository.save(os);
    }


    // =========================
    // CANCELAR ORDEM DE SERVICO
    // =========================
    public OrdemServico cancelarOrdemServico(UUID id) {
        OrdemServico os = buscarPorId(id);

        os.setStatus(StatusOS.CANCELADA);

        return repository.save(os);
    }


    // =========================
    // DELETAR ORDEM DE SERVICO
    // =========================
    public OrdemServico deletarOrdemServico(UUID id) {
        OrdemServico os = buscarPorId(id);
        os.setStatus(StatusOS.CANCELADA);
        return repository.save(os);
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
    private BigDecimal calcularTotal(OrdemServico os) {
        return os.getValorPecas()
                .add(os.getValorMaoDeObra())
                .subtract(os.getDesconto());
    }



}
