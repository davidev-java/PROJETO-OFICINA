package com.novanest.projetooficina.mapper;

import com.novanest.projetooficina.dto.usuario.UsuarioResponseDTO;
import com.novanest.projetooficina.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponseDTO toDTO(Usuario usuario);
}