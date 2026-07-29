package com.novanest.projetooficina.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "veiculos")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Veiculo {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // PLACA
    @Column(nullable = false, unique = true, length = 7)
    private String placa;

    // MARCA DO CARRO
    @Column(nullable = false, length = 25)
    private String marca;

    // MODELO DO CARRO
    @Column(nullable = false, length = 80)
    private String modelo;

    //ANO DO CARRO
    @Column(nullable = false)
    private Integer ano;

    // COR DO CARRO
    @Column(nullable = false, length = 25)
    private String cor;

    // QUILOMETRAGEM DO CARRO
    @Column(nullable = false)
    private Integer quilometragem;

    // CLIENTE RESPONSAVEL DO CARRO
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // MARCA DADOS DO MODO DEMO (resetados periodicamente, isolados dos dados reais)
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean demo = false;
}
