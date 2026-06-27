package com.novanest.projetooficina.dto.cliente;

import com.novanest.projetooficina.enums.TipoCliente;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClienteRequestDTO {

    private String nome;
    private TipoCliente tipoCliente;

    private String cpf;
    private String cnpj;

    private String telefone;
    private String email;
    private String endereco;

    private LocalDate dataNascimento;

    private String observacoes;
    private String whatsapp;
    private String cidade;
    private String estado;
}
