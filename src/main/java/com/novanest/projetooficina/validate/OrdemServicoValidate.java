package com.novanest.projetooficina.validate;


import com.novanest.projetooficina.entity.OrdemServico;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class OrdemServicoValidate {

    // =========================
    // VALIDAR ORDEM DE SERVIÇO
    // =========================
    public void validarOrdemServico(OrdemServico os) {

        // =========================
        // CLIENTE
        // Verifica se foi informado um cliente
        // e se ele possui um ID válido
        // =========================
        if (os.getCliente() == null || os.getCliente().getId() == null) {
            throw new IllegalArgumentException("Cliente obrigatório!");
        }

        // =========================
        // VEÍCULO
        // Verifica se foi informado um veículo
        // e se ele possui um ID válido
        // =========================
        if (os.getVeiculo() == null || os.getVeiculo().getId() == null) {
            throw new IllegalArgumentException("Veículo obrigatório!");
        }

        // =========================
        // DESCRIÇÃO DO PROBLEMA
        // Verifica se a descrição foi preenchida
        // =========================
        if (os.getDescricaoProblema() == null ||
                os.getDescricaoProblema().isBlank()) {

            throw new IllegalArgumentException(
                    "Descrição do problema obrigatória!");
        }

        // =========================
        // TAMANHO DA DESCRIÇÃO
        // Evita textos muito grandes
        // =========================
        if (os.getDescricaoProblema().length() > 500) {
            throw new IllegalArgumentException(
                    "Descrição muito longa!");
        }

        // =========================
        // VALOR DA MÃO DE OBRA
        // Não permite valores negativos
        // =========================
        if (os.getValorMaoDeObra() != null &&
                os.getValorMaoDeObra().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Valor da mão de obra inválido!");
        }

        // =========================
        // VALOR DAS PEÇAS
        // Não permite valores negativos
        // =========================
        if (os.getValorPecas() != null &&
                os.getValorPecas().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Valor das peças inválido!");
        }

        // =========================
        // DESCONTO
        // Não permite desconto negativo
        // =========================
        if (os.getDesconto() != null &&
                os.getDesconto().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Desconto inválido!");
        }

        // =========================
        // DESCONTO MAIOR QUE O TOTAL
        // Não permite que o desconto
        // seja maior que peças + mão de obra
        // =========================
        BigDecimal valorPecas = os.getValorPecas() != null
                ? os.getValorPecas()
                : BigDecimal.ZERO;

        BigDecimal valorMaoDeObra = os.getValorMaoDeObra() != null
                ? os.getValorMaoDeObra()
                : BigDecimal.ZERO;

        BigDecimal desconto = os.getDesconto() != null
                ? os.getDesconto()
                : BigDecimal.ZERO;

        if (desconto.compareTo(valorPecas.add(valorMaoDeObra)) > 0) {
            throw new IllegalArgumentException(
                    "O desconto não pode ser maior que o valor total da ordem de serviço!");
        }
    }
}
