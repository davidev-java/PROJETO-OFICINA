import { useAuth } from '../context/AuthContext'

// Aviso bem visivel de que o usuario esta no modo demo publico (admin_demo
// ou visitante) - dados fake, resetados periodicamente, isolados dos reais.
export function DemoBanner() {
  const { usuario } = useAuth()

  if (!usuario?.demo) return null

  return (
    <div className="faixa-demo">
      🚧 Modo demonstração — dados fictícios, resetados automaticamente. Nada aqui é real.
    </div>
  )
}
