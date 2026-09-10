import { createContext, useContext, useEffect, useState } from 'react'
import { demoApi } from '../api/demo'
import { relatoriosApi } from '../api/relatorios'
import { useAuth } from './AuthContext'

// Carrega os dados do modo demo UMA vez, quando o usuario demo entra, e
// guarda em memoria. Sem isso, cada troca de aba disparava tres requisicoes
// novas e o overlay de "acordando o servidor" piscava no meio da navegacao.
const DemoDadosContext = createContext(null)

const VAZIO = { clientes: [], veiculos: [], ordens: [], relatorio: null }

function valorOuPadrao(resultado, padrao) {
  return resultado.status === 'fulfilled' ? resultado.value : padrao
}

export function DemoDadosProvider({ children }) {
  const { usuario } = useAuth()
  const ehDemo = Boolean(usuario?.demo)

  const [dados, setDados] = useState(VAZIO)
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState(null)

  // allSettled, nao all: cada parte da tela e independente. Com Promise.all,
  // uma unica chamada que falhasse (ex.: backend antigo sem /demo/relatorios)
  // derrubava tambem as listas que ja tinham carregado, e a tela ficava zerada.
  async function carregar() {
    setCarregando(true)
    setErro(null)

    const [clientes, veiculos, ordens, relatorio] = await Promise.allSettled([
      demoApi.listarClientes(),
      demoApi.listarVeiculos(),
      demoApi.listarOrdens(),
      relatoriosApi.resumoDemo(),
    ])

    setDados({
      clientes: valorOuPadrao(clientes, []),
      veiculos: valorOuPadrao(veiculos, []),
      ordens: valorOuPadrao(ordens, []),
      relatorio: valorOuPadrao(relatorio, null),
    })

    const falhas = [clientes, veiculos, ordens, relatorio].filter((r) => r.status === 'rejected')
    setErro(falhas.length > 0 ? falhas[0].reason?.message ?? 'Não foi possível carregar tudo.' : null)
    setCarregando(false)
  }

  useEffect(() => {
    if (!ehDemo) {
      setDados(VAZIO)
      return
    }
    carregar()
  }, [ehDemo])

  return (
    <DemoDadosContext.Provider value={{ ...dados, carregando, erro, recarregar: carregar }}>
      {children}
    </DemoDadosContext.Provider>
  )
}

export function useDemoDados() {
  return useContext(DemoDadosContext)
}
