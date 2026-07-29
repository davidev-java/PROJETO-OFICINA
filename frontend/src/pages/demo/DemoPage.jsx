import { useEffect, useState } from 'react'
import { demoApi } from '../../api/demo'
import { useAuth } from '../../context/AuthContext'

const STATUS_LABEL = {
  ABERTA: 'Aberta',
  EM_ANDAMENTO: 'Em andamento',
  FINALIZADA: 'Finalizada',
  CANCELADA: 'Cancelada',
}

// Painel unico do modo demo: mostra os 3 recursos lado a lado. Leitura pra
// qualquer um (inclusive o "visitante"), exclusao disponivel só quando
// logado como admin_demo (a API já reforça isso, aqui é só UX).
export function DemoPage() {
  const { usuario } = useAuth()
  const podeEscrever = usuario?.role === 'ADMIN'

  const [clientes, setClientes] = useState([])
  const [veiculos, setVeiculos] = useState([])
  const [ordens, setOrdens] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  function carregar() {
    setCarregando(true)
    Promise.all([demoApi.listarClientes(), demoApi.listarVeiculos(), demoApi.listarOrdens()])
      .then(([c, v, o]) => {
        setClientes(c)
        setVeiculos(v)
        setOrdens(o)
      })
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }

  useEffect(carregar, [])

  async function excluirCliente(id) {
    try {
      await demoApi.deletarCliente(id)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  if (carregando) return <p>Carregando dados demo...</p>

  return (
    <div>
      <div className="pagina-topo">
        <h1>Painel Demo</h1>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <p>
        Você está logado como <strong>{podeEscrever ? 'admin_demo' : 'visitante'}</strong>.
        {podeEscrever ? ' Você pode criar/excluir dados demo.' : ' Acesso somente leitura.'}
      </p>

      <h2 style={{ marginTop: 28 }}>Clientes ({clientes.length})</h2>
      <div className="tabela-wrap">
        <table className="tabela">
          <thead>
            <tr>
              <th>Nome</th><th>Tipo</th><th>Telefone</th><th>Cidade/UF</th>{podeEscrever && <th></th>}
            </tr>
          </thead>
          <tbody>
            {clientes.map((c) => (
              <tr key={c.id}>
                <td>{c.nome}</td>
                <td>{c.tipoCliente === 'PESSOA_FISICA' ? 'PF' : 'PJ'}</td>
                <td>{c.telefone}</td>
                <td>{c.cidade}/{c.estado}</td>
                {podeEscrever && (
                  <td><button className="link-perigo" onClick={() => excluirCliente(c.id)}>Excluir</button></td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <h2 style={{ marginTop: 28 }}>Veículos ({veiculos.length})</h2>
      <div className="tabela-wrap">
        <table className="tabela">
          <thead>
            <tr><th>Placa</th><th>Marca/Modelo</th><th>Ano</th><th>Cor</th></tr>
          </thead>
          <tbody>
            {veiculos.map((v) => (
              <tr key={v.id}>
                <td className="mono">{v.placa}</td>
                <td>{v.marca} {v.modelo}</td>
                <td>{v.ano}</td>
                <td>{v.cor}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <h2 style={{ marginTop: 28 }}>Ordens de Serviço ({ordens.length})</h2>
      <div className="tabela-wrap">
        <table className="tabela">
          <thead>
            <tr><th>Número</th><th>Cliente</th><th>Status</th><th>Total</th></tr>
          </thead>
          <tbody>
            {ordens.map((o) => (
              <tr key={o.id}>
                <td className="mono">{o.numeroOs}</td>
                <td>{o.cliente?.nome}</td>
                <td><span className={`status status-${o.status?.toLowerCase()}`}>{STATUS_LABEL[o.status]}</span></td>
                <td>R$ {Number(o.valorTotal ?? 0).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
