import { useEffect, useState } from 'react'
import { ordensServicoApi } from '../../api/ordensServico'

const STATUS_LABEL = {
  ABERTA: 'Aberta',
  EM_ANDAMENTO: 'Em andamento',
  FINALIZADA: 'Finalizada',
  CANCELADA: 'Cancelada',
}

export function MinhasOrdensPage() {
  const [ordens, setOrdens] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    ordensServicoApi.minhas()
      .then(setOrdens)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }, [])

  return (
    <div>
      <div className="pagina-topo">
        <h1>Minhas Ordens de Serviço</h1>
      </div>

      {erro && <p className="erro">{erro}</p>}
      {carregando && <p>Carregando...</p>}

      {!carregando && ordens.length === 0 && !erro && (
        <p>Nenhuma ordem de serviço encontrada pro seu cadastro ainda.</p>
      )}

      {!carregando && ordens.length > 0 && (
        <div className="tabela-wrap">
          <table className="tabela">
            <thead>
              <tr>
                <th>Número</th>
                <th>Veículo</th>
                <th>Status</th>
                <th>Descrição</th>
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              {ordens.map((o) => (
                <tr key={o.id}>
                  <td className="mono">{o.numeroOs}</td>
                  <td className="mono">{o.veiculo?.placa}</td>
                  <td><span className={`status status-${o.status?.toLowerCase()}`}>{STATUS_LABEL[o.status]}</span></td>
                  <td>{o.descricaoProblema}</td>
                  <td>R$ {Number(o.valorTotal ?? 0).toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
