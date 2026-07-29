import { useEffect, useState } from 'react'

// O backend roda no plano free do Render: depois de 15min parado, "dorme" e
// a primeira requisição demora 30-50s pra acordar. Sem essa tela, parece
// que o site travou. Só mostra o aviso de demora depois de um tempinho,
// pra não piscar em toda navegação normal (que é instantânea).
export function TelaInicializando() {
  const [mostrarAviso, setMostrarAviso] = useState(false)

  useEffect(() => {
    const timer = setTimeout(() => setMostrarAviso(true), 1800)
    return () => clearTimeout(timer)
  }, [])

  return (
    <div className="tela-inicializando">
      <div className="spinner" />
      {mostrarAviso && (
        <>
          <h2>O servidor está iniciando</h2>
          <p>Isso pode levar até um minuto na primeira visita — o backend "dorme" quando fica um tempo sem uso.</p>
        </>
      )}
    </div>
  )
}
