import { useEffect, useState } from 'react'
import { clientesApi } from '../api/clientes'
import { veiculosApi } from '../api/veiculos'
import { ordensServicoApi } from '../api/ordensServico'
import { useAuth } from '../context/AuthContext'

const STAFF = ['ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO']

export function Dashboard() {
  const { usuario } = useAuth()
  const ehStaff = STAFF.includes(usuario?.role)

  const [resumo, setResumo] = useState({ clientes: 0, veiculos: 0, ordens: 0 })
  const [carregando, setCarregando] = useState(ehStaff)

  useEffect(() => {
    if (!ehStaff) return

    Promise.all([clientesApi.listar(), veiculosApi.listar(), ordensServicoApi.listar()])
      .then(([clientes, veiculos, ordens]) => {
        setResumo({ clientes: clientes.length, veiculos: veiculos.length, ordens: ordens.length })
      })
      .finally(() => setCarregando(false))
  }, [ehStaff])

  return (
    <div className="pagina">
      <h1>Olá, {usuario?.nome}</h1>

      {!ehStaff && <p>Bem-vindo(a) à oficina. Em breve você poderá acompanhar suas ordens de serviço por aqui.</p>}

      {ehStaff && (
        <div className="cards-resumo">
          <div className="card-resumo">
            <span className="numero">{carregando ? '...' : resumo.clientes}</span>
            <span>Clientes</span>
          </div>
          <div className="card-resumo">
            <span className="numero">{carregando ? '...' : resumo.veiculos}</span>
            <span>Veículos</span>
          </div>
          <div className="card-resumo">
            <span className="numero">{carregando ? '...' : resumo.ordens}</span>
            <span>Ordens de Serviço</span>
          </div>
        </div>
      )}
    </div>
  )
}
