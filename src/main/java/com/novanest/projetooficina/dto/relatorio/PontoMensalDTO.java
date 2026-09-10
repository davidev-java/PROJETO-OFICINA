package com.novanest.projetooficina.dto.relatorio;

import java.math.BigDecimal;

// Um ponto do grafico de faturamento. "competencia" e o ano-mes (2026-08),
// "rotulo" e o que aparece embaixo da barra (ago/26).
public record PontoMensalDTO(String competencia, String rotulo, BigDecimal valor) {
}
