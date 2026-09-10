import { useEffect, useState } from 'react'
import { demoApi } from '../../api/demo'
import { useAuth } from '../../context/AuthContext'
import { STATUS_LABEL, formatarMoeda } from './comum'

export function DemoOrdensPage() {
  const { usuario } = useAuth()
  const podeEscrever = usuario?.role === 'ADMIN'

  const [ordens, setOrdens] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  function carregar() {
    setCarregando(true)
    demoApi.listarOrdens()
      .then(setOrdens)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }

  useEffect(carregar, [])

  async function excluir(id) {
    try {
      await demoApi.deletarOrdemServico(id)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <div>
      <div className="pagina-topo">
        <div>
          <span className="rotulo">Demonstração</span>
          <h1>Ordens de Serviço</h1>
        </div>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <div className="tabela-wrap">
        <table className="tabela">
          <thead>
            <tr>
              <th>Número</th><th>Cliente</th><th>Veículo</th><th>Status</th><th>Total</th>{podeEscrever && <th></th>}
            </tr>
          </thead>
          <tbody>
            {carregando && <tr><td colSpan={6}>Carregando...</td></tr>}
            {!carregando && ordens.length === 0 && <tr><td colSpan={6}>Nenhuma ordem no demo.</td></tr>}
            {ordens.map((o) => (
              <tr key={o.id}>
                <td className="mono">{o.numeroOs}</td>
                <td>{o.cliente?.nome}</td>
                <td className="mono">{o.veiculo?.placa}</td>
                <td><span className={`status status-${o.status?.toLowerCase()}`}>{STATUS_LABEL[o.status]}</span></td>
                <td className="mono">{formatarMoeda(o.valorTotal)}</td>
                {podeEscrever && (
                  <td className="acoes">
                    <button className="link-perigo" onClick={() => excluir(o.id)}>Excluir</button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
