import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { clientesApi } from '../../api/clientes'

export function ClientesPage() {
  const [clientes, setClientes] = useState([])
  const [busca, setBusca] = useState('')
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  function carregar() {
    setCarregando(true)
    clientesApi
      .listar()
      .then(setClientes)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }

  useEffect(carregar, [])

  async function buscar(e) {
    e.preventDefault()
    setErro(null)

    if (!busca.trim()) {
      carregar()
      return
    }

    try {
      setCarregando(true)
      setClientes(await clientesApi.buscarPorNome(busca))
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  async function excluir(id) {
    if (!confirm('Excluir este cliente?')) return

    try {
      await clientesApi.deletar(id)
      setClientes((atual) => atual.filter((c) => c.id !== id))
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <div className="pagina">
      <div className="pagina-topo">
        <h1>Clientes</h1>
        <Link className="botao" to="/clientes/novo">+ Novo cliente</Link>
      </div>

      <form className="busca" onSubmit={buscar}>
        <input
          placeholder="Buscar por nome..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
        <button type="submit">Buscar</button>
      </form>

      {erro && <p className="erro">{erro}</p>}
      {carregando && <p>Carregando...</p>}

      {!carregando && (
        <table className="tabela">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Tipo</th>
              <th>Telefone</th>
              <th>Email</th>
              <th>Cidade/UF</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {clientes.map((c) => (
              <tr key={c.id}>
                <td>{c.nome}</td>
                <td>{c.tipoCliente === 'PESSOA_FISICA' ? 'Pessoa Física' : 'Pessoa Jurídica'}</td>
                <td>{c.telefone}</td>
                <td>{c.email}</td>
                <td>{c.cidade ? `${c.cidade}/${c.estado ?? ''}` : '-'}</td>
                <td className="acoes">
                  <Link to={`/clientes/${c.id}`}>Editar</Link>
                  <button className="link-perigo" onClick={() => excluir(c.id)}>Excluir</button>
                </td>
              </tr>
            ))}
            {clientes.length === 0 && (
              <tr><td colSpan={6}>Nenhum cliente encontrado.</td></tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  )
}
