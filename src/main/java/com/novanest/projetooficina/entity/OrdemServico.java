package com.novanest.projetooficina.entity;

import com.novanest.projetooficina.enums.StatusOS;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ordens_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 30, updatable = false)
    private String numeroOs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    @NotNull
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private StatusOS status;

    @Column(nullable = false, updatable = false)
    private LocalDate dataAbertura;

    private LocalDate dataFechamento;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String descricaoProblema;

    @PositiveOrZero
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMaoDeObra;

    @PositiveOrZero
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPecas;

    @PositiveOrZero
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal desconto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @PrePersist
    public void prePersist() {
        this.dataAbertura = LocalDate.now();

        if (this.numeroOs == null) {
            this.numeroOs = "OS-" + System.currentTimeMillis();
        }

        if (this.status == null) {
            this.status = StatusOS.ABERTA;
        }
    }
}