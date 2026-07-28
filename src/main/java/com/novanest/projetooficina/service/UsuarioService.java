package com.novanest.projetooficina.service;

import com.novanest.projetooficina.dto.usuario.UsuarioResponseDTO;
import com.novanest.projetooficina.entity.Usuario;
import com.novanest.projetooficina.enums.Role;
import com.novanest.projetooficina.exception.UsuarioNaoEncontradoException;
import com.novanest.projetooficina.mapper.UsuarioMapper;
import com.novanest.projetooficina.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    // =========================
    // BUSCAR POR EMAIL (usado no login/JWT)
    // =========================
    public Usuario buscarPorEmailEntity(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    // =========================
    // BUSCAR OU CRIAR A PARTIR DO LOGIN GOOGLE
    // =========================
    public Usuario buscarOuCriarPorEmail(String email, String nome, String avatarUrl) {
        return usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    Usuario novo = new Usuario();
                    novo.setEmail(email);
                    novo.setNome(nome);
                    novo.setAvatarUrl(avatarUrl);
                    novo.setRole(Role.CLIENTE);
                    return usuarioRepository.save(novo);
                });
    }

    // =========================
    // LISTAR TODOS
    // =========================
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toDTO)
                .toList();
    }

    // =========================
    // BUSCAR POR EMAIL (DTO)
    // =========================
    public UsuarioResponseDTO buscarPorEmail(String email) {
        return usuarioMapper.toDTO(buscarPorEmailEntity(email));
    }

    // =========================
    // ATUALIZAR ROLE (apenas ADMIN)
    // =========================
    public UsuarioResponseDTO atualizarRole(Long id, Role novaRole) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        usuario.setRole(novaRole);

        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }
}