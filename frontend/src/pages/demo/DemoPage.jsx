import { Link } from 'react-router-dom'
import { Users, Car, Wrench } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import { useDemoDados } from '../../context/DemoDadosContext'
import { STATUS_LABEL, formatarMoeda } from './comum'

// Visao geral do modo demo: numeros no topo e as ultimas OS. As listas
// completas de cada recurso viram tela propria, acessivel pelo menu lateral.
export function DemoPage() {
  const { usuario } = useAuth()
  const podeEscrever = usuario?.role === 'ADMIN'

  const { clientes, veiculos, ordens, relatorio, carregando, erro } = useDemoDados()

  // Os numeros vem prontos do endpoint de relatorio (agregacao no banco);
  // aqui so se escolhe quais mostrar.
  const emAberto = (relatorio?.ordensPorStatus ?? [])
    .filter((c) => c.status === 'ABERTA' || c.status === 'EM_ANDAMENTO')
    .reduce((soma, c) => soma + c.quantidade, 0)

  const ultimasOrdens = ordens.slice(0, 5)

  return (
    <div>
      <div className="pagina-topo">
        <div>
          <span className="rotulo">Demonstração</span>
          <h1>Visão geral</h1>
        </div>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <p className="demo-explicacao">
        Você entrou como <strong>{podeEscrever ? 'admin_demo' : 'visitante'}</strong>
        {podeEscrever ? ' — pode criar e excluir registros.' : ' — acesso somente leitura.'}{' '}
        Tudo aqui é fictício, isolado do sistema real e recriado a cada 6 horas.
      </p>

      <div className="cards-resumo">
        <div className="card-resumo">
          <span className="numero">{carregando ? '—' : emAberto}</span>
          <span className="rotulo">OS em aberto</span>
        </div>
        <div className="card-resumo">
          <span className="numero">{carregando ? '—' : veiculos.length}</span>
          <span className="rotulo">Veículos cadastrados</span>
        </div>
        <div className="card-resumo">
          <span className="numero numero-mono">{carregando ? '—' : formatarMoeda(relatorio?.faturamentoTotal)}</span>
          <span className="rotulo">Total finalizado</span>
        </div>
      </div>

      <div className="demo-atalhos">
        <Link to="/demo/clientes" className="demo-atalho">
          <Users size={18} strokeWidth={1.75} />
          <span className="demo-atalho-numero">{carregando ? '—' : clientes.length}</span>
          <span className="rotulo">Clientes</span>
        </Link>
        <Link to="/demo/veiculos" className="demo-atalho">
          <Car size={18} strokeWidth={1.75} />
          <span className="demo-atalho-numero">{carregando ? '—' : veiculos.length}</span>
          <span className="rotulo">Veículos</span>
        </Link>
        <Link to="/demo/ordens" className="demo-atalho">
          <Wrench size={18} strokeWidth={1.75} />
          <span className="demo-atalho-numero">{carregando ? '—' : ordens.length}</span>
          <span className="rotulo">Ordens de Serviço</span>
        </Link>
      </div>

      <h2 className="demo-secao-titulo">Últimas ordens de serviço</h2>
      <div className="tabela-wrap">
        <table className="tabela">
          <thead>
            <tr><th>Número</th><th>Cliente</th><th>Status</th><th>Total</th></tr>
          </thead>
          <tbody>
            {carregando && <tr><td colSpan={4}>Carregando...</td></tr>}
            {!carregando && ultimasOrdens.length === 0 && <tr><td colSpan={4}>Nenhuma ordem no demo.</td></tr>}
            {ultimasOrdens.map((o) => (
              <tr key={o.id}>
                <td className="mono">{o.numeroOs}</td>
                <td>{o.cliente?.nome}</td>
                <td><span className={`status status-${o.status?.toLowerCase()}`}>{STATUS_LABEL[o.status]}</span></td>
                <td className="mono">{formatarMoeda(o.valorTotal)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
