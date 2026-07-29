import { useEffect, useState } from 'react'
import { ordensServicoApi } from '../api/ordensServico'
import { useAuth } from '../context/AuthContext'

const STAFF = ['ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO']

function formatarMoeda(valor) {
  return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 })
}

export function Dashboard() {
  const { usuario } = useAuth()
  const ehStaff = STAFF.includes(usuario?.role)

  const [kpis, setKpis] = useState({ abertas: 0, naOficina: 0, faturamento: 0 })
  const [carregando, setCarregando] = useState(ehStaff)

  useEffect(() => {
    if (!ehStaff) return

    ordensServicoApi.listar()
      .then((ordens) => {
        const agora = new Date()
        const abertas = ordens.filter((o) => o.status === 'ABERTA').length
        const naOficina = ordens.filter((o) => o.status === 'EM_ANDAMENTO').length

        const faturamento = ordens
          .filter((o) => {
            if (o.status !== 'FINALIZADA' || !o.dataFechamento) return false
            const data = new Date(o.dataFechamento)
            return data.getMonth() === agora.getMonth() && data.getFullYear() === agora.getFullYear()
          })
          .reduce((soma, o) => soma + Number(o.valorTotal ?? 0), 0)

        setKpis({ abertas, naOficina, faturamento })
      })
      .finally(() => setCarregando(false))
  }, [ehStaff])

  return (
    <div>
      <div className="pagina-topo">
        <div>
          <span className="rotulo">Painel</span>
          <h1>Olá, {usuario?.nome}</h1>
        </div>
      </div>

      {!ehStaff && <p>Bem-vindo(a) à oficina. Em breve você poderá acompanhar suas ordens de serviço por aqui.</p>}

      {ehStaff && (
        <div className="cards-resumo">
          <div className="card-resumo">
            <span className="numero">{carregando ? '—' : kpis.abertas}</span>
            <span className="rotulo">OS Abertas</span>
          </div>
          <div className="card-resumo">
            <span className="numero">{carregando ? '—' : kpis.naOficina}</span>
            <span className="rotulo">Veículos na Oficina</span>
          </div>
          <div className="card-resumo">
            <span className="numero numero-mono">{carregando ? '—' : formatarMoeda(kpis.faturamento)}</span>
            <span className="rotulo">Faturamento do Mês</span>
          </div>
        </div>
      )}
    </div>
  )
}
