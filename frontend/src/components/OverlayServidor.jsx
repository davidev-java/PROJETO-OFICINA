import { useEffect, useState } from 'react'
import { assinarStatusServidor } from '../api/http'

// Tela que cobre o app inteiro enquanto o backend esta acordando (cold start
// do plano free). Existe pra que o visitante nunca veja a tela de "starting"
// da hospedagem nem uma interface pela metade: o que aparece e sempre a
// identidade do projeto.
export function OverlayServidor() {
  const [acordando, setAcordando] = useState(false)
  const [segundos, setSegundos] = useState(0)

  useEffect(() => assinarStatusServidor(setAcordando), [])

  useEffect(() => {
    if (!acordando) {
      setSegundos(0)
      return
    }
    const intervalo = setInterval(() => setSegundos((s) => s + 1), 1000)
    return () => clearInterval(intervalo)
  }, [acordando])

  if (!acordando) return null

  return (
    <div className="overlay-servidor" role="status" aria-live="polite">
      <div className="spinner" />
      <h2>Acordando o servidor</h2>
      <p>
        O backend fica em suspenso quando ninguém usa e leva até um minuto pra subir.
        Não precisa recarregar a página — assim que ele responder, o app abre sozinho.
      </p>
      <span className="overlay-servidor-tempo">{String(segundos).padStart(2, '0')}s</span>
    </div>
  )
}
