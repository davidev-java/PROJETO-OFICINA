import { useEffect, useState } from 'react'
import { veiculosApi } from '../../api/veiculos'

export function MeusVeiculosPage() {
  const [veiculos, setVeiculos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    veiculosApi.meus()
      .then(setVeiculos)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }, [])

  return (
    <div>
      <div className="pagina-topo">
        <h1>Meus Veículos</h1>
      </div>

      {erro && <p className="erro">{erro}</p>}
      {carregando && <p>Carregando...</p>}

      {!carregando && veiculos.length === 0 && !erro && (
        <p>Nenhum veículo vinculado ao seu cadastro ainda. Fale com a oficina se isso não estiver certo.</p>
      )}

      {!carregando && veiculos.length > 0 && (
        <div className="tabela-wrap">
          <table className="tabela">
            <thead>
              <tr>
                <th>Placa</th>
                <th>Marca/Modelo</th>
                <th>Ano</th>
                <th>Cor</th>
                <th>KM</th>
              </tr>
            </thead>
            <tbody>
              {veiculos.map((v) => (
                <tr key={v.id}>
                  <td className="mono">{v.placa}</td>
                  <td>{v.marca} {v.modelo}</td>
                  <td>{v.ano}</td>
                  <td>{v.cor}</td>
                  <td>{v.quilometragem}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
