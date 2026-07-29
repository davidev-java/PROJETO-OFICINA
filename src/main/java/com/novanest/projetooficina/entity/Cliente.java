package com.novanest.projetooficina.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.novanest.projetooficina.enums.TipoCliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cliente {

    // ID DO CLIENTE
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // NOME DO CLIENTE
    @NotBlank
    @Size(min = 5, max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    // TIPO DE CLIENTE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipoCliente;

    // CPF DO CLIENTE
    @Column(unique = true, length = 11)
    private String cpf;

    //CNPJ DO CLIENTE
    @Column(unique = true, length = 14)
    private String cnpj;

    // TELEFONE DO CLIENTE
    @NotBlank
    @Column(nullable = false, length = 15)
    private String telefone;

    // EMAIL DO CLIENTE
    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // ENDERECO DO CLIENTE
    @NotBlank
    @Column(nullable = false, length = 150)
    private String endereco;

    // DATA DE CADASTRO DO CLIENTE
    @Column(nullable = false, updatable = false)
    private LocalDate dataCadastro;

    // DATA DE NASCIMENTO DO CLIENTE
    @Column(nullable = false)
    private LocalDate dataNascimento;

    // OBSERVACOES DO CLIENTE(OPCIONAL)
    @Column(length = 255)
    private String observacoes;

    // WHATSAPP DO CLIENTE(OPCIONAL)
    @Column(length = 20)
    private String whatsapp;

    // CIDADE DO CLIENTE(OPCIONAL)
    @Column(length = 80)
    private String cidade;

    // ESTADO DO CLIENTE(OPCIONAL)
    @Column(length = 2)
    private String estado;

    // VEICULOS
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Veiculo> veiculos;

    // MARCA DADOS DO MODO DEMO (resetados periodicamente, isolados dos dados reais)
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean demo = false;

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDate.now();
    }
}