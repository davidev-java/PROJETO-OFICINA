package com.novanest.projetooficina.dto.ordem_servico;

import com.novanest.projetooficina.dto.cliente.ClienteResponseDTO;
import com.novanest.projetooficina.dto.veiculo.VeiculoResponseDTO;
import com.novanest.projetooficina.enums.StatusOS;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class OrdemServicoResponseDTO {

    private UUID id;
    private String numeroOs;

    private ClienteResponseDTO cliente;
    private VeiculoResponseDTO veiculo;

    private StatusOS status;

    private LocalDate dataAbertura;
    private LocalDate dataFechamento;

    private String descricaoProblema;

    private BigDecimal valorMaoDeObra;
    private BigDecimal valorPecas;
    private BigDecimal desconto;
    private BigDecimal valorTotal;

}
