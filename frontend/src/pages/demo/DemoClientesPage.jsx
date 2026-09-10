import { useEffect, useState } from 'react'
import { demoApi } from '../../api/demo'
import { useAuth } from '../../context/AuthContext'

export function DemoClientesPage() {
  const { usuario } = useAuth()
  const podeEscrever = usuario?.role === 'ADMIN'

  const [clientes, setClientes] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  function carregar() {
    setCarregando(true)
    demoApi.listarClientes()
      .then(setClientes)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }

  useEffect(carregar, [])

  async function excluir(id) {
    try {
      await demoApi.deletarCliente(id)
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
          <h1>Clientes</h1>
        </div>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <div className="tabela-wrap">
        <table className="tabela">
          <thead>
            <tr>
              <th>Nome</th><th>Tipo</th><th>Telefone</th><th>Cidade/UF</th>{podeEscrever && <th></th>}
            </tr>
          </thead>
          <tbody>
            {carregando && <tr><td colSpan={5}>Carregando...</td></tr>}
            {!carregando && clientes.length === 0 && <tr><td colSpan={5}>Nenhum cliente no demo.</td></tr>}
            {clientes.map((c) => (
              <tr key={c.id}>
                <td>{c.nome}</td>
                <td>{c.tipoCliente === 'PESSOA_FISICA' ? 'PF' : 'PJ'}</td>
                <td className="mono">{c.telefone}</td>
                <td>{c.cidade}/{c.estado}</td>
                {podeEscrever && (
                  <td className="acoes">
                    <button className="link-perigo" onClick={() => excluir(c.id)}>Excluir</button>
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
