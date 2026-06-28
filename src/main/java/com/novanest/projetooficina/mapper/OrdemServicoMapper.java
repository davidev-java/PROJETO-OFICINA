package com.novanest.projetooficina.mapper;

import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoRequestDTO;
import com.novanest.projetooficina.dto.ordem_servico.OrdemServicoResponseDTO;
import com.novanest.projetooficina.entity.OrdemServico;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrdemServicoMapper {

    OrdemServico toEntity(OrdemServicoRequestDTO ordemServicoRequestDTO);

    OrdemServicoResponseDTO toDTO(OrdemServico ordemServico);
}
