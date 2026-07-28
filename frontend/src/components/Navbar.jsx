import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const STAFF = ['ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO']

export function Navbar() {
  const { usuario, logout } = useAuth()

  if (!usuario) return null

  const ehStaff = STAFF.includes(usuario.role)

  return (
    <header className="navbar">
      <div className="navbar-brand">Oficina</div>

      <nav className="navbar-links">
        <NavLink to="/">Início</NavLink>
        {ehStaff && <NavLink to="/clientes">Clientes</NavLink>}
        {ehStaff && <NavLink to="/veiculos">Veículos</NavLink>}
        {ehStaff && <NavLink to="/ordens">Ordens de Serviço</NavLink>}
      </nav>

      <div className="navbar-usuario">
        {usuario.avatarUrl && <img src={usuario.avatarUrl} alt={usuario.nome} className="avatar" />}
        <span>{usuario.nome} <small>({usuario.role})</small></span>
        <button onClick={logout}>Sair</button>
      </div>
    </header>
  )
}
