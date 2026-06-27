package com.novanest.projetooficina.mapper;

import com.novanest.projetooficina.dto.cliente.ClienteRequestDTO;
import com.novanest.projetooficina.dto.cliente.ClienteResponseDTO;
import com.novanest.projetooficina.entity.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper{

    Cliente toEntity(ClienteRequestDTO clienteDTO);

    ClienteResponseDTO toDTO(Cliente cliente);
}
