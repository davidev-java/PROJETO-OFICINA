import { useDemoDados } from '../../context/DemoDadosContext'
import { PainelRelatorios } from '../../components/graficos/PainelRelatorios'

export function DemoRelatoriosPage() {
  const { relatorio, carregando, erro } = useDemoDados()

  return (
    <div>
      <div className="pagina-topo">
        <div>
          <span className="rotulo">Demonstração</span>
          <h1>Relatórios</h1>
        </div>
      </div>

      {erro && <p className="erro">{erro}</p>}
      {carregando && <p>Calculando os números...</p>}

      <PainelRelatorios resumo={relatorio} />
    </div>
  )
}
