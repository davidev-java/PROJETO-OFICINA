package com.novanest.projetooficina.repository;

import com.novanest.projetooficina.entity.OrdemServico;
import com.novanest.projetooficina.enums.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}
