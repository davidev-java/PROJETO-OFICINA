import { GraficoFaturamento } from './GraficoFaturamento'
import { GraficoStatus } from './GraficoStatus'
import { GraficoMarcas } from './GraficoMarcas'
import { formatarMoeda, formatarNumero } from './formato'

// Os quatro blocos da tela de relatorios, com o mesmo peso. Serve tanto pro
// sistema real quanto pro modo demo - muda so de onde vem o resumo.
export function PainelRelatorios({ resumo }) {
  if (!resumo) return null

  return (
    <div className="painel-relatorios">
      <section className="bloco-relatorio bloco-largo">
        <header>
          <h2>Faturamento</h2>
          <p>Ordens finalizadas nos últimos 6 meses</p>
        </header>
        <GraficoFaturamento pontos={resumo.faturamentoMensal} />
      </section>

      <section className="bloco-relatorio">
        <header>
          <h2>Ordens por status</h2>
          <p>{formatarNumero(resumo.totalOrdens)} ordens no total</p>
        </header>
        <GraficoStatus contagens={resumo.ordensPorStatus} />
      </section>

      <section className="bloco-relatorio">
        <header>
          <h2>Ticket médio</h2>
          <p>Média por ordem finalizada</p>
        </header>
        <div className="ticket">
          <span className="ticket-valor">{formatarMoeda(resumo.ticketMedio)}</span>
          <dl className="ticket-detalhe">
            <div>
              <dt>Total faturado</dt>
              <dd>{formatarMoeda(resumo.faturamentoTotal)}</dd>
            </div>
            <div>
              <dt>Ordens finalizadas</dt>
              <dd>{formatarNumero(resumo.ordensFinalizadas)}</dd>
            </div>
          </dl>
        </div>
      </section>

      <section className="bloco-relatorio">
        <header>
          <h2>Marcas mais atendidas</h2>
          <p>Por número de ordens</p>
        </header>
        <GraficoMarcas marcas={resumo.topMarcas} />
      </section>
    </div>
  )
}
