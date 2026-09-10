package com.novanest.projetooficina.repository;

import com.novanest.projetooficina.entity.OrdemServico;
import com.novanest.projetooficina.enums.StatusOS;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, UUID> {

    List<OrdemServico> findByClienteId(UUID clienteId);

    List<OrdemServico> findByVeiculoId(UUID veiculoId);

    List<OrdemServico> findByStatus(StatusOS status);

    Optional<OrdemServico> findByNumeroOs(String numeroOs);

    boolean existsById(UUID id);

    // =========================
    // MODO DEMO
    // =========================
    // EntityGraph traz cliente e veiculo no mesmo select. Sem isso, listar as
    // OS do demo dispara um select por linha pra cada relacao (N+1) - com 200+
    // ordens sao centenas de idas ao banco.
    @EntityGraph(attributePaths = {"cliente", "veiculo"})
    List<OrdemServico> findByDemoTrue();

    // Apagar em bloco: um unico DELETE em vez de um por linha.
    @Modifying
    @Transactional
    @Query("DELETE FROM OrdemServico o WHERE o.demo = true")
    void apagarTodasDemo();

    // =========================
    // RELATORIOS
    // =========================
    // Agregacao acontece no banco (COUNT/SUM/AVG + GROUP BY): o Java so recebe
    // o resultado ja somado, em vez de trazer todas as OS pra contar na memoria.

    // Quantas OS existem em cada status.
    @Query("""
            SELECT o.status, COUNT(o)
            FROM OrdemServico o
            WHERE o.demo = :demo
            GROUP BY o.status
            """)
    List<Object[]> contarPorStatus(@Param("demo") boolean demo);

    // Faturamento por mes, olhando so o que foi finalizado a partir de :desde.
    // year()/month() sao funcoes do Hibernate que funcionam tanto no
    // PostgreSQL (producao) quanto no H2 (testes).
    @Query("""
            SELECT year(o.dataFechamento), month(o.dataFechamento), SUM(o.valorTotal)
            FROM OrdemServico o
            WHERE o.demo = :demo
              AND o.status = com.novanest.projetooficina.enums.StatusOS.FINALIZADA
              AND o.dataFechamento >= :desde
            GROUP BY year(o.dataFechamento), month(o.dataFechamento)
            ORDER BY year(o.dataFechamento), month(o.dataFechamento)
            """)
    List<Object[]> faturamentoPorMes(@Param("demo") boolean demo, @Param("desde") LocalDate desde);

    // Marcas que mais aparecem nas OS - o JOIN sai da propria relacao da entidade.
    @Query("""
            SELECT v.marca, COUNT(o)
            FROM OrdemServico o
            JOIN o.veiculo v
            WHERE o.demo = :demo
            GROUP BY v.marca
            ORDER BY COUNT(o) DESC, v.marca ASC
            """)
    List<Object[]> contarPorMarca(@Param("demo") boolean demo);

    // Total faturado e ticket medio das OS finalizadas.
    @Query("""
            SELECT COALESCE(SUM(o.valorTotal), 0), COALESCE(AVG(o.valorTotal), 0), COUNT(o)
            FROM OrdemServico o
            WHERE o.demo = :demo
              AND o.status = com.novanest.projetooficina.enums.StatusOS.FINALIZADA
            """)
    List<Object[]> totaisFinalizadas(@Param("demo") boolean demo);

    long countByDemo(boolean demo);

}
