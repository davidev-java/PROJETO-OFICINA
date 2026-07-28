import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ordensServicoApi } from '../../api/ordensServico'

const STATUS_LABEL = {
  ABERTA: 'Aberta',
  EM_ANDAMENTO: 'Em andamento',
  FINALIZADA: 'Finalizada',
  CANCELADA: 'Cancelada',
}

export function OrdensPage() {
  const [ordens, setOrdens] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  function carregar() {
    setCarregando(true)
    ordensServicoApi
      .listar()
      .then(setOrdens)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }

  useEffect(carregar, [])

  async function finalizar(id) {
    try {
      await ordensServicoApi.finalizar(id)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function cancelar(id) {
    if (!confirm('Cancelar esta ordem de serviço?')) return
    try {
      await ordensServicoApi.cancelar(id)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function excluir(id) {
    if (!confirm('Excluir esta ordem de serviço?')) return
    try {
      await ordensServicoApi.deletar(id)
      setOrdens((atual) => atual.filter((o) => o.id !== id))
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <div className="pagina">
      <div className="pagina-topo">
        <h1>Ordens de Serviço</h1>
        <Link className="botao" to="/ordens/nova">+ Nova OS</Link>
      </div>

      {erro && <p className="erro">{erro}</p>}
      {carregando && <p>Carregando...</p>}

      {!carregando && (
        <table className="tabela">
          <thead>
            <tr>
              <th>Número</th>
              <th>Cliente</th>
              <th>Veículo</th>
              <th>Status</th>
              <th>Total</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {ordens.map((o) => (
              <tr key={o.id}>
                <td>{o.numeroOs}</td>
                <td>{o.cliente?.nome}</td>
                <td>{o.veiculo?.placa}</td>
                <td><span className={`status status-${o.status?.toLowerCase()}`}>{STATUS_LABEL[o.status]}</span></td>
                <td>R$ {Number(o.valorTotal ?? 0).toFixed(2)}</td>
                <td className="acoes">
                  <Link to={`/ordens/${o.id}`}>Editar</Link>
                  {o.status !== 'FINALIZADA' && o.status !== 'CANCELADA' && (
                    <>
                      <button className="link" onClick={() => finalizar(o.id)}>Finalizar</button>
                      <button className="link-perigo" onClick={() => cancelar(o.id)}>Cancelar</button>
                    </>
                  )}
                  <button className="link-perigo" onClick={() => excluir(o.id)}>Excluir</button>
                </td>
              </tr>
            ))}
            {ordens.length === 0 && (
              <tr><td colSpan={6}>Nenhuma ordem de serviço encontrada.</td></tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  )
}
