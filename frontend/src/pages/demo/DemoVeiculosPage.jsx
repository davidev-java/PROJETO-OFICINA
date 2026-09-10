import { useState } from 'react'
import { demoApi } from '../../api/demo'
import { useAuth } from '../../context/AuthContext'
import { useDemoDados } from '../../context/DemoDadosContext'

export function DemoVeiculosPage() {
  const { usuario } = useAuth()
  const podeEscrever = usuario?.role === 'ADMIN'

  const { veiculos, carregando, erro: erroCarga, recarregar } = useDemoDados()
  const [erroAcao, setErroAcao] = useState(null)
  const erro = erroAcao ?? erroCarga


  async function excluir(id) {
    try {
      await demoApi.deletarVeiculo(id)
      await recarregar()
    } catch (e) {
      setErroAcao(e.message)
    }
  }

  return (
    <div>
      <div className="pagina-topo">
        <div>
          <span className="rotulo">Demonstração</span>
          <h1>Veículos</h1>
        </div>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <div className="tabela-wrap">
        <table className="tabela">
          <thead>
            <tr>
              <th>Placa</th><th>Marca/Modelo</th><th>Ano</th><th>Cor</th>{podeEscrever && <th></th>}
            </tr>
          </thead>
          <tbody>
            {carregando && <tr><td colSpan={5}>Carregando...</td></tr>}
            {!carregando && veiculos.length === 0 && <tr><td colSpan={5}>Nenhum veículo no demo.</td></tr>}
            {veiculos.map((v) => (
              <tr key={v.id}>
                <td className="mono">{v.placa}</td>
                <td>{v.marca} {v.modelo}</td>
                <td className="mono">{v.ano}</td>
                <td>{v.cor}</td>
                {podeEscrever && (
                  <td className="acoes">
                    <button className="link-perigo" onClick={() => excluir(v.id)}>Excluir</button>
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
