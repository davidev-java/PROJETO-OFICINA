import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { veiculosApi } from '../../api/veiculos'

export function VeiculosPage() {
  const [veiculos, setVeiculos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  function carregar() {
    setCarregando(true)
    veiculosApi
      .listar()
      .then(setVeiculos)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }

  useEffect(carregar, [])

  async function excluir(id) {
    if (!confirm('Excluir este veículo?')) return

    try {
      await veiculosApi.deletar(id)
      setVeiculos((atual) => atual.filter((v) => v.id !== id))
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <div className="pagina">
      <div className="pagina-topo">
        <h1>Veículos</h1>
        <Link className="botao" to="/veiculos/novo">+ Novo veículo</Link>
      </div>

      {erro && <p className="erro">{erro}</p>}
      {carregando && <p>Carregando...</p>}

      {!carregando && (
        <table className="tabela">
          <thead>
            <tr>
              <th>Placa</th>
              <th>Marca/Modelo</th>
              <th>Ano</th>
              <th>Cor</th>
              <th>KM</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {veiculos.map((v) => (
              <tr key={v.id}>
                <td>{v.placa}</td>
                <td>{v.marca} {v.modelo}</td>
                <td>{v.ano}</td>
                <td>{v.cor}</td>
                <td>{v.quilometragem}</td>
                <td className="acoes">
                  <Link to={`/veiculos/${v.id}`}>Editar</Link>
                  <button className="link-perigo" onClick={() => excluir(v.id)}>Excluir</button>
                </td>
              </tr>
            ))}
            {veiculos.length === 0 && (
              <tr><td colSpan={6}>Nenhum veículo encontrado.</td></tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  )
}
