import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function ProtectedRoute({ children, rolesPermitidas }) {
  const { usuario, carregando } = useAuth()

  if (carregando) {
    return <div className="carregando">Carregando...</div>
  }

  if (!usuario) {
    return <Navigate to="/login" replace />
  }

  if (rolesPermitidas && !rolesPermitidas.includes(usuario.role)) {
    return <div className="sem-permissao">Você não tem permissão para acessar esta página.</div>
  }

  return children
}
