import { useState } from 'react'
import { formatarMoedaCurta, formatarMoeda } from './formato'

// Grafico de area do faturamento mes a mes. SVG na mao mesmo: sao 6 pontos,
// nao vale carregar uma biblioteca de graficos pra isso.
const LARGURA = 640
const ALTURA = 220
const MARGEM = { topo: 28, base: 34, lado: 16 }

export function GraficoFaturamento({ pontos = [] }) {
  const [ativo, setAtivo] = useState(null)

  if (pontos.length === 0) return <p className="grafico-vazio">Sem faturamento no período.</p>

  const valores = pontos.map((p) => Number(p.valor ?? 0))
  const maximo = Math.max(...valores, 1)

  const areaUtil = ALTURA - MARGEM.topo - MARGEM.base
  const passo = (LARGURA - MARGEM.lado * 2) / Math.max(pontos.length - 1, 1)

  const coordenadas = valores.map((valor, i) => ({
    x: MARGEM.lado + i * passo,
    y: MARGEM.topo + areaUtil - (valor / maximo) * areaUtil,
  }))

  const linha = coordenadas.map((c, i) => `${i === 0 ? 'M' : 'L'}${c.x.toFixed(1)},${c.y.toFixed(1)}`).join(' ')
  const area = `${linha} L${coordenadas.at(-1).x.toFixed(1)},${(ALTURA - MARGEM.base).toFixed(1)} L${coordenadas[0].x.toFixed(1)},${(ALTURA - MARGEM.base).toFixed(1)} Z`

  const destacado = ativo ?? pontos.length - 1

  return (
    <div className="grafico">
      <svg viewBox={`0 0 ${LARGURA} ${ALTURA}`} className="grafico-svg" role="img"
           aria-label="Faturamento dos últimos meses">
        <defs>
          <linearGradient id="preenchimentoFaturamento" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="var(--roxo-lilas)" stopOpacity="0.32" />
            <stop offset="100%" stopColor="var(--roxo-lilas)" stopOpacity="0" />
          </linearGradient>
        </defs>

        {/* linha de base, pro olho ter onde apoiar */}
        <line x1={MARGEM.lado} y1={ALTURA - MARGEM.base} x2={LARGURA - MARGEM.lado} y2={ALTURA - MARGEM.base}
              className="grafico-eixo" />

        <path d={area} fill="url(#preenchimentoFaturamento)" className="grafico-area" />
        <path d={linha} className="grafico-linha" vectorEffect="non-scaling-stroke" />

        {coordenadas.map((c, i) => (
          <g key={pontos[i].competencia}>
            <line x1={c.x} y1={MARGEM.topo} x2={c.x} y2={ALTURA - MARGEM.base}
                  className={`grafico-guia${i === destacado ? ' visivel' : ''}`} />
            <circle cx={c.x} cy={c.y} r={i === destacado ? 5 : 3}
                    className={`grafico-ponto${i === destacado ? ' ativo' : ''}`} />
            <text x={c.x} y={ALTURA - 12} textAnchor="middle" className="grafico-rotulo">
              {pontos[i].rotulo}
            </text>
            {/* faixa invisivel: da area de hover generosa pro ponto */}
            <rect x={c.x - passo / 2} y={0} width={passo} height={ALTURA} fill="transparent"
                  onMouseEnter={() => setAtivo(i)} onMouseLeave={() => setAtivo(null)} />
          </g>
        ))}

        <text x={coordenadas[destacado].x} y={Math.max(coordenadas[destacado].y - 14, 16)}
              textAnchor={destacado === 0 ? 'start' : destacado === pontos.length - 1 ? 'end' : 'middle'}
              className="grafico-valor">
          {formatarMoedaCurta(valores[destacado])}
        </text>
      </svg>

      <p className="grafico-legenda">
        {pontos[destacado].rotulo}: <strong>{formatarMoeda(valores[destacado])}</strong>
      </p>
    </div>
  )
}
