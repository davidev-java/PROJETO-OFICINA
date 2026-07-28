package com.novanest.projetooficina.dto.ordem_servico;

import com.novanest.projetooficina.enums.StatusOS;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrdemServicoRequestDTO {

    private UUID clienteId;
    private UUID veiculoId;
    private StatusOS statusOS;
    private String descricaoProblema;

    private BigDecimal valorMaoDeObra;
    private BigDecimal valorPecas;
    private BigDecimal desconto;
    private BigDecimal valorTotal;
}
