package com.novanest.projetooficina.repository;

import com.novanest.projetooficina.entity.Cliente;
import com.novanest.projetooficina.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {

    List<Veiculo> findByMarcaContainingIgnoreCase(String marca);

    List<Veiculo> findByModeloContainingIgnoreCase(String modelo);

    List<Veiculo> findByCliente_id(UUID cliente_id);

    Optional<Veiculo> findByPlaca(String placa);

    boolean existsByPlaca(String placa);

    // =========================
    // MODO DEMO
    // =========================
    List<Veiculo> findByDemoTrue();

}
