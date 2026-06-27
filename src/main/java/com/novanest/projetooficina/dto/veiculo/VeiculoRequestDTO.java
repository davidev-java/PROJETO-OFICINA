package com.novanest.projetooficina.dto.veiculo;

import com.novanest.projetooficina.entity.Cliente;
import lombok.Data;

import java.util.UUID;

@Data
public class VeiculoRequestDTO {

    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private Integer quilometragem;

    private UUID clienteId;
}
