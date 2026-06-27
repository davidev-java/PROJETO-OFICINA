package com.novanest.projetooficina.dto.cliente;

import com.novanest.projetooficina.enums.TipoCliente;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ClienteResponseDTO {

    private UUID id;
    private String nome;
    private TipoCliente tipoCliente;

    private String cpf;
    private String cnpj;

    private String telefone;
    private String email;
    private String endereco;

    private LocalDate dataCadastro;
    private LocalDate dataNascimento;

    private String observacoes;
    private String whatsapp;
    private String cidade;
    private String estado;
}
