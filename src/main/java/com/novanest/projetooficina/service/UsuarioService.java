package com.novanest.projetooficina.service;

import com.novanest.projetooficina.dto.usuario.RegistroRequestDTO;
import com.novanest.projetooficina.dto.usuario.UsuarioResponseDTO;
import com.novanest.projetooficina.entity.Cliente;
import com.novanest.projetooficina.entity.Usuario;
import com.novanest.projetooficina.enums.Role;
import com.novanest.projetooficina.exception.CredenciaisInvalidasException;
import com.novanest.projetooficina.exception.UsuarioNaoEncontradoException;
import com.novanest.projetooficina.mapper.UsuarioMapper;
import com.novanest.projetooficina.repository.ClienteRepository;
import com.novanest.projetooficina.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    // Email que devem entrar automaticamente como ADMIN no primeiro login -
    // substitui promoção manual via UPDATE direto no banco de produção.
    @Value("${app.bootstrap-admin-email:}")
    private String bootstrapAdminEmail;

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
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    Usuario novo = new Usuario();
                    novo.setEmail(email);
                    novo.setNome(nome);
                    novo.setAvatarUrl(avatarUrl);
                    novo.setRole(ehBootstrapAdmin(email) ? Role.ADMIN : Role.CLIENTE);
                    return usuarioRepository.save(novo);
                });

        return linkarClienteSeExistir(usuario);
    }

    // =========================
    // LINKAR AO CADASTRO DE CLIENTE (mesmo email), se existir
    // Usado pra role CLIENTE enxergar "meus veiculos"/"minhas OS":
    // sem isso nao ha como saber de quem e o usuario logado.
    // =========================
    private Usuario linkarClienteSeExistir(Usuario usuario) {
        if (usuario.getCliente() != null) {
            return usuario;
        }

        return clienteRepository.findByEmail(usuario.getEmail())
                .map(cliente -> {
                    usuario.setCliente(cliente);
                    return usuarioRepository.save(usuario);
                })
                .orElse(usuario);
    }

    // =========================
    // REGISTRAR (LOGIN LOCAL - EMAIL/SENHA)
    // =========================
    public Usuario registrar(RegistroRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Usuario novo = new Usuario();
        novo.setEmail(dto.getEmail());
        novo.setNome(dto.getNome());
        novo.setSenha(passwordEncoder.encode(dto.getSenha()));
        novo.setRole(ehBootstrapAdmin(dto.getEmail()) ? Role.ADMIN : Role.CLIENTE);

        Usuario salvo = usuarioRepository.save(novo);
        return linkarClienteSeExistir(salvo);
    }

    private boolean ehBootstrapAdmin(String email) {
        return !bootstrapAdminEmail.isBlank() && bootstrapAdminEmail.equalsIgnoreCase(email);
    }

    // =========================
    // AUTENTICAR (LOGIN LOCAL - EMAIL/SENHA)
    // =========================
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new CredenciaisInvalidasException("Email ou senha inválidos"));

        if (usuario.getSenha() == null || !passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Email ou senha inválidos");
        }

        return usuario;
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