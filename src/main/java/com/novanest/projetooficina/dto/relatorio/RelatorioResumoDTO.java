package com.novanest.projetooficina.dto.relatorio;

import java.math.BigDecimal;
import java.util.List;

// Tudo que a tela de relatorios precisa, numa resposta so.
// Os numeros ja vem calculados pelo banco (COUNT/SUM/AVG + GROUP BY) -
// o frontend nao soma nada, so desenha.
public record RelatorioResumoDTO(
        List<PontoMensalDTO> faturamentoMensal,
        List<ContagemStatusDTO> ordensPorStatus,
        List<ContagemMarcaDTO> topMarcas,
        BigDecimal faturamentoTotal,
        BigDecimal ticketMedio,
        long totalOrdens,
        long ordensFinalizadas
) {
}
