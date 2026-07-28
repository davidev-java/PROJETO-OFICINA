package com.novanest.projetooficina.controller;

import com.novanest.projetooficina.dto.usuario.UsuarioResponseDTO;
import com.novanest.projetooficina.enums.Role;
import com.novanest.projetooficina.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    // USUÁRIO LOGADO
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(service.buscarPorEmail(authentication.getName()));
    }

    // LISTAR TODOS (apenas ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UsuarioResponseDTO> listarUsuarios() {
        return service.listarTodos();
    }

    // ATUALIZAR ROLE DE UM USUÁRIO (apenas ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UsuarioResponseDTO> atualizarRole(
            @PathVariable Long id,
            @RequestParam Role role) {

        return ResponseEntity.ok(service.atualizarRole(id, role));
    }
}