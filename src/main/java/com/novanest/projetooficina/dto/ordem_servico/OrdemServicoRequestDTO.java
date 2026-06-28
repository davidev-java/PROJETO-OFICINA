package com.novanest.projetooficina.dto.ordem_servico;

import com.novanest.projetooficina.entity.Cliente;
import com.novanest.projetooficina.entity.Veiculo;
import com.novanest.projetooficina.enums.StatusOS;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdemServicoRequestDTO {

    private Cliente cliente;
    private Veiculo veiculo;
    private StatusOS statusOS;
    private String descricaoProblema;

    private BigDecimal valorMaoDeObra;
    private BigDecimal valorPecas;
    private BigDecimal desconto;
    private BigDecimal valorTotal;
}
