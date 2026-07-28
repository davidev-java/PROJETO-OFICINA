import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { setToken } from '../api/http'
import { useAuth } from '../context/AuthContext'

export function LoginCallback() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { recarregar } = useAuth()
  const [erro, setErro] = useState(null)

  useEffect(() => {
    const token = searchParams.get('token')

    if (!token) {
      setErro('Token não recebido. Tente fazer login novamente.')
      return
    }

    setToken(token)
    recarregar().then(() => navigate('/', { replace: true }))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  if (erro) {
    return (
      <div className="tela-login">
        <div className="card-login">
          <p>{erro}</p>
          <a href="/login">Voltar para o login</a>
        </div>
      </div>
    )
  }

  return <div className="carregando">Entrando...</div>
}
