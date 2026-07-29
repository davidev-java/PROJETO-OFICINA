package com.novanest.projetooficina.dto.usuario;

import com.novanest.projetooficina.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String email;
    private String nome;
    private String avatarUrl;
    private Role role;
    private boolean demo;
    private LocalDateTime criadoEm;
}