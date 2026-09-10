import { useEffect, useState } from 'react'
import { relatoriosApi } from '../../api/relatorios'
import { PainelRelatorios } from '../../components/graficos/PainelRelatorios'

export function RelatoriosPage() {
  const [resumo, setResumo] = useState(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    relatoriosApi.resumo()
      .then(setResumo)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }, [])

  return (
    <div>
      <div className="pagina-topo">
        <div>
          <span className="rotulo">Análise</span>
          <h1>Relatórios</h1>
        </div>
      </div>

      {erro && <p className="erro">{erro}</p>}
      {carregando && <p>Calculando os números...</p>}

      <PainelRelatorios resumo={resumo} />
    </div>
  )
}
