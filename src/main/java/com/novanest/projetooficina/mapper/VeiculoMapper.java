package com.novanest.projetooficina.mapper;


import com.novanest.projetooficina.dto.veiculo.VeiculoRequestDTO;
import com.novanest.projetooficina.dto.veiculo.VeiculoResponseDTO;
import com.novanest.projetooficina.entity.Veiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VeiculoMapper {

    Veiculo toEntity(VeiculoRequestDTO veiculoRequestDTO);

    @Mapping(source = "cliente.id", target = "clienteId")
    VeiculoResponseDTO toDTO(Veiculo veiculo);


}
