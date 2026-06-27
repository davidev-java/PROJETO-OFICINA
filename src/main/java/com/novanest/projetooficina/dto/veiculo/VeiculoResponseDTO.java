package com.novanest.projetooficina.dto.veiculo;

import lombok.Data;

import java.util.UUID;

@Data
public class VeiculoResponseDTO {

    private UUID id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private Integer quilometragem;

    private UUID clienteId;
    
}
