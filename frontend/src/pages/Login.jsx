import { Navigate } from 'react-router-dom'
import { urlLoginGoogle } from '../api/http'
import { useAuth } from '../context/AuthContext'

export function Login() {
  const { usuario, carregando } = useAuth()

  if (!carregando && usuario) {
    return <Navigate to="/" replace />
  }

  return (
    <div className="tela-login">
      <div className="card-login">
        <h1>Oficina</h1>
        <p>Sistema de gerenciamento de oficina mecânica</p>
        <a className="botao-google" href={urlLoginGoogle()}>
          Entrar com Google
        </a>
      </div>
    </div>
  )
}
