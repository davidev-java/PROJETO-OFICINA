package com.novanest.projetooficina.mapper;

import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoRequestDTO;
import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoResponseDTO;
import com.novanest.projetooficina.entity.OrdemServico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrdemServicoMapper {

    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "veiculo", ignore = true)
    @Mapping(target = "status", ignore = true)
    OrdemServico toEntity(OrdemServicoRequestDTO ordemServicoRequestDTO);

    OrdemServicoResponseDTO toDTO(OrdemServico ordemServico);
}