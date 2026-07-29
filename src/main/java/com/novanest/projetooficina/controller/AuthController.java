package com.novanest.projetooficina.controller;

import com.novanest.projetooficina.dto.usuario.AuthResponseDTO;
import com.novanest.projetooficina.dto.usuario.LoginRequestDTO;
import com.novanest.projetooficina.dto.usuario.RegistroRequestDTO;
import com.novanest.projetooficina.entity.Usuario;
import com.novanest.projetooficina.mapper.UsuarioMapper;
import com.novanest.projetooficina.security.JwtService;
import com.novanest.projetooficina.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Login local (email/senha), adicional ao login Google - mesmo contrato de
// JWT usado no fluxo OAuth2, so que devolvido no corpo da resposta em vez
// de query string de redirect.
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO dto) {
        Usuario usuario = usuarioService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(gerarResposta(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        Usuario usuario = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
        return ResponseEntity.ok(gerarResposta(usuario));
    }

    private AuthResponseDTO gerarResposta(Usuario usuario) {
        String token = jwtService.gerarToken(usuario);
        return new AuthResponseDTO(token, usuarioMapper.toDTO(usuario));
    }
}
