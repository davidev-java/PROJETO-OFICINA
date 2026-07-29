package com.novanest.projetooficina.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================
    // ENTIDADE NÃO ENCONTRADA (404)
    // =========================
    @ExceptionHandler({
            ClienteNaoEncontradoException.class,
            VeiculoNaoEncontradoException.class,
            OrdemServicoNaoEncontradaException.class,
            UsuarioNaoEncontradoException.class
    })
    public ResponseEntity<Object> handleNaoEncontrado(RuntimeException ex) {
        return corpoErro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // =========================
    // REGRA DE NEGÓCIO INVÁLIDA (400)
    // =========================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return corpoErro(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // =========================
    // ACESSO NEGADO (403)
    // =========================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return corpoErro(HttpStatus.FORBIDDEN, "Você não tem permissão para realizar esta ação");
    }

    // =========================
    // ROTA INEXISTENTE (404)
    // =========================
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleRotaInexistente(NoResourceFoundException ex) {
        return corpoErro(HttpStatus.NOT_FOUND, "Rota não encontrada");
    }

    // =========================
    // VALIDAÇÃO DE CAMPOS (@Valid) (400)
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage()));

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", HttpStatus.BAD_REQUEST.value());
        corpo.put("erro", "Erro de validação");
        corpo.put("campos", erros);

        return ResponseEntity.badRequest().body(corpo);
    }

    // =========================
    // QUALQUER OUTRO ERRO (500)
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenerico(Exception ex) {
        return corpoErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno: " + ex.getMessage());
    }

    private ResponseEntity<Object> corpoErro(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", status.value());
        corpo.put("erro", mensagem);

        return ResponseEntity.status(status).body(corpo);
    }
}