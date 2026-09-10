import { formatarNumero } from './formato'

// Ranking das marcas que mais passam pela oficina.
export function GraficoMarcas({ marcas = [] }) {
  if (marcas.length === 0) return <p className="grafico-vazio">Nenhum veículo atendido ainda.</p>

  const maximo = Math.max(...marcas.map((m) => m.quantidade), 1)

  return (
    <ul className="barras">
      {marcas.map((m) => (
        <li key={m.marca} className="barra-linha">
          <span className="barra-nome">{m.marca}</span>
          <span className="barra-trilho">
            <span className="barra-preenchida" style={{ '--largura': `${(m.quantidade / maximo) * 100}%` }} />
          </span>
          <span className="barra-numero">{formatarNumero(m.quantidade)}</span>
        </li>
      ))}
    </ul>
  )
}
