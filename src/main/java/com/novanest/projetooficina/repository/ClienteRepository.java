package com.novanest.projetooficina.repository;

import com.novanest.projetooficina.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    // =========================
    // VERIFICAÇÕES (USADAS NO SERVICE)
    // =========================
    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByCnpj(String cnpj);

    // =========================
    // BUSCAS ÚTEIS
    // =========================
    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByCpf(String cpf);

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    // =========================
    // MODO DEMO
    // =========================
    List<Cliente> findByDemoTrue();

    @Modifying
    @Transactional
    @Query("DELETE FROM Cliente c WHERE c.demo = true")
    void apagarTodosDemo();
}
