import { createContext, useContext, useEffect, useState } from 'react'
import { demoApi } from '../api/demo'
import { relatoriosApi } from '../api/relatorios'
import { useAuth } from './AuthContext'

// Carrega os dados do modo demo UMA vez, quando o usuario demo entra, e
// guarda em memoria. Sem isso, cada troca de aba disparava tres requisicoes
// novas e o overlay de "acordando o servidor" piscava no meio da navegacao.
const DemoDadosContext = createContext(null)

const VAZIO = { clientes: [], veiculos: [], ordens: [], relatorio: null }

export function DemoDadosProvider({ children }) {
  const { usuario } = useAuth()
  const ehDemo = Boolean(usuario?.demo)

  const [dados, setDados] = useState(VAZIO)
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState(null)

  async function carregar() {
    setCarregando(true)
    setErro(null)

    try {
      const [clientes, veiculos, ordens, relatorio] = await Promise.all([
        demoApi.listarClientes(),
        demoApi.listarVeiculos(),
        demoApi.listarOrdens(),
        relatoriosApi.resumoDemo(),
      ])
      setDados({ clientes, veiculos, ordens, relatorio })
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
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
