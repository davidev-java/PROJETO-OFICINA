import { formatarNumero } from './formato'

// Barras horizontais das OS por status. Um acento so (roxo) em intensidades
// diferentes - a identidade do projeto nao usa cor semantica.
const ROTULOS = {
  ABERTA: 'Aberta',
  EM_ANDAMENTO: 'Em andamento',
  FINALIZADA: 'Finalizada',
  CANCELADA: 'Cancelada',
}

const INTENSIDADE = {
  ABERTA: 'var(--roxo-lilas)',
  EM_ANDAMENTO: 'var(--roxo-secundario)',
  FINALIZADA: 'var(--roxo)',
  CANCELADA: 'var(--roxo-profundo)',
}

export function GraficoStatus({ contagens = [] }) {
  const maximo = Math.max(...contagens.map((c) => c.quantidade), 1)
  const total = contagens.reduce((soma, c) => soma + c.quantidade, 0)

  if (total === 0) return <p className="grafico-vazio">Nenhuma ordem de serviço registrada.</p>

  return (
    <ul className="barras">
      {contagens.map((c) => (
        <li key={c.status} className="barra-linha">
          <span className="barra-nome">{ROTULOS[c.status] ?? c.status}</span>
          <span className="barra-trilho">
            <span
              className="barra-preenchida"
              style={{ '--largura': `${(c.quantidade / maximo) * 100}%`, background: INTENSIDADE[c.status] }}
            />
          </span>
          <span className="barra-numero">{formatarNumero(c.quantidade)}</span>
        </li>
      ))}
    </ul>
  )
}
