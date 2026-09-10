package com.novanest.projetooficina.service;

import com.novanest.projetooficina.dto.relatorio.ContagemMarcaDTO;
import com.novanest.projetooficina.dto.relatorio.ContagemStatusDTO;
import com.novanest.projetooficina.dto.relatorio.PontoMensalDTO;
import com.novanest.projetooficina.dto.relatorio.RelatorioResumoDTO;
import com.novanest.projetooficina.enums.StatusOS;
import com.novanest.projetooficina.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private static final int MESES_NO_GRAFICO = 6;
    private static final int TAMANHO_RANKING = 5;

    private static final String[] MESES_ABREVIADOS = {
            "jan", "fev", "mar", "abr", "mai", "jun",
            "jul", "ago", "set", "out", "nov", "dez"
    };

    private final OrdemServicoRepository ordemServicoRepository;

    // ===== RESUMO GERAL =====
    // O mesmo relatorio serve pro sistema real (demo = false) e pro modo demo
    // (demo = true) - o que muda e so o filtro que vai pras queries.
    public RelatorioResumoDTO gerarResumo(boolean demo) {
        return new RelatorioResumoDTO(
                montarFaturamentoMensal(demo),
                montarOrdensPorStatus(demo),
                montarTopMarcas(demo),
                totalFinalizado(demo),
                ticketMedio(demo),
                ordemServicoRepository.countByDemo(demo),
                quantidadeFinalizadas(demo)
        );
    }

    // ===== FATURAMENTO DOS ULTIMOS MESES =====
    // O banco devolve so os meses que tiveram OS finalizada. Aqui a serie e
    // completada com zero nos meses vazios, senao o grafico ficaria com buraco.
    private List<PontoMensalDTO> montarFaturamentoMensal(boolean demo) {
        YearMonth mesInicial = YearMonth.now().minusMonths(MESES_NO_GRAFICO - 1L);
        LocalDate desde = mesInicial.atDay(1);

        Map<YearMonth, BigDecimal> porMes = new HashMap<>();
        for (Object[] linha : ordemServicoRepository.faturamentoPorMes(demo, desde)) {
            YearMonth competencia = YearMonth.of(numero(linha[0]).intValue(), numero(linha[1]).intValue());
            porMes.put(competencia, valor(linha[2]));
        }

        List<PontoMensalDTO> serie = new ArrayList<>();
        for (int i = 0; i < MESES_NO_GRAFICO; i++) {
            YearMonth competencia = mesInicial.plusMonths(i);
            serie.add(new PontoMensalDTO(
                    competencia.toString(),
                    rotularMes(competencia),
                    porMes.getOrDefault(competencia, BigDecimal.ZERO)
            ));
        }

        return serie;
    }

    // ===== OS POR STATUS =====
    // Status sem nenhuma OS tambem entra na lista (com zero), pra que o grafico
    // mostre sempre as quatro faixas.
    private List<ContagemStatusDTO> montarOrdensPorStatus(boolean demo) {
        Map<StatusOS, Long> contagens = new HashMap<>();
        for (Object[] linha : ordemServicoRepository.contarPorStatus(demo)) {
            contagens.put((StatusOS) linha[0], numero(linha[1]).longValue());
        }

        List<ContagemStatusDTO> resultado = new ArrayList<>();
        for (StatusOS status : StatusOS.values()) {
            resultado.add(new ContagemStatusDTO(status, contagens.getOrDefault(status, 0L)));
        }

        return resultado;
    }

    // ===== MARCAS QUE MAIS APARECEM =====
    private List<ContagemMarcaDTO> montarTopMarcas(boolean demo) {
        return ordemServicoRepository.contarPorMarca(demo).stream()
                .limit(TAMANHO_RANKING)
                .map(linha -> new ContagemMarcaDTO((String) linha[0], numero(linha[1]).longValue()))
                .toList();
    }

    private BigDecimal totalFinalizado(boolean demo) {
        return valor(primeiraLinhaDeTotais(demo)[0]);
    }

    private BigDecimal ticketMedio(boolean demo) {
        return valor(primeiraLinhaDeTotais(demo)[1]).setScale(2, RoundingMode.HALF_UP);
    }

    private long quantidadeFinalizadas(boolean demo) {
        return numero(primeiraLinhaDeTotais(demo)[2]).longValue();
    }

    private Object[] primeiraLinhaDeTotais(boolean demo) {
        List<Object[]> linhas = ordemServicoRepository.totaisFinalizadas(demo);
        return linhas.isEmpty() ? new Object[]{BigDecimal.ZERO, BigDecimal.ZERO, 0L} : linhas.get(0);
    }

    // O tipo exato que o banco devolve num COUNT/SUM varia (Long, Integer,
    // BigInteger, BigDecimal), entao a conversao passa sempre por Number.
    private Number numero(Object valor) {
        return valor == null ? 0 : (Number) valor;
    }

    private BigDecimal valor(Object valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        return valor instanceof BigDecimal decimal ? decimal : BigDecimal.valueOf(((Number) valor).doubleValue());
    }

    private String rotularMes(YearMonth competencia) {
        return MESES_ABREVIADOS[competencia.getMonthValue() - 1] + "/" + (competencia.getYear() % 100);
    }
}
