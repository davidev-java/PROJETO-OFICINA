package com.novanest.projetooficina.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mesmo contrato usado no redirect do login Google (?token=...), só que
// devolvido direto no corpo da resposta em vez de query string, já que
// aqui é uma chamada de API (fetch), não navegação de browser.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private UsuarioResponseDTO usuario;
}
