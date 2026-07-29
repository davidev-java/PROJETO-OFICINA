import { useEffect, useState } from 'react'
import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { clientesApi } from '../api/clientes'
import { veiculosApi } from '../api/veiculos'
import { ordensServicoApi } from '../api/ordensServico'
import { usuariosApi } from '../api/usuarios'

const STAFF = ['ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO']

function ItemMenu({ to, label, contador }) {
  return (
    <NavLink to={to} className={({ isActive }) => `sidebar-item${isActive ? ' ativo' : ''}`} end>
      <span>{label}</span>
      {contador != null && <span className="sidebar-contador">{contador}</span>}
    </NavLink>
  )
}

export function Sidebar({ aberta, onFechar }) {
  const { usuario, logout } = useAuth()
  const [contagens, setContagens] = useState({})

  const ehDemo = Boolean(usuario?.demo)
  const ehStaff = !ehDemo && STAFF.includes(usuario?.role)
  const ehAdmin = !ehDemo && usuario?.role === 'ADMIN'

  useEffect(() => {
    if (!ehStaff) return

    Promise.all([clientesApi.listar(), veiculosApi.listar(), ordensServicoApi.listar()])
      .then(([clientes, veiculos, ordens]) => {
        setContagens((c) => ({ ...c, clientes: clientes.length, veiculos: veiculos.length, ordens: ordens.length }))
      })
      .catch(() => {})
  }, [ehStaff])

  useEffect(() => {
    if (!ehAdmin) return

    usuariosApi.listar()
      .then((usuarios) => setContagens((c) => ({ ...c, usuarios: usuarios.length })))
      .catch(() => {})
  }, [ehAdmin])

  if (!usuario) return null

  return (
    <aside className={`sidebar${aberta ? ' aberta' : ''}`}>
      <div className="sidebar-marca">
        <span className="ponto" />
        Oficina
      </div>

      <nav onClick={onFechar}>
        {ehDemo && (
          <div className="sidebar-secao">
            <div className="sidebar-secao-titulo">Demonstração</div>
            <ItemMenu to="/" label="Painel Demo" />
          </div>
        )}

        {!ehDemo && (
          <div className="sidebar-secao">
            <div className="sidebar-secao-titulo">Operação</div>
            <ItemMenu to="/" label="Dashboard" />
            {ehStaff && <ItemMenu to="/ordens" label="Ordens de Serviço" contador={contagens.ordens} />}
            {!ehStaff && <ItemMenu to="/minhas-ordens" label="Minhas Ordens" />}
          </div>
        )}

        {ehStaff && (
          <div className="sidebar-secao">
            <div className="sidebar-secao-titulo">Cadastros</div>
            <ItemMenu to="/clientes" label="Clientes" contador={contagens.clientes} />
            <ItemMenu to="/veiculos" label="Veículos" contador={contagens.veiculos} />
          </div>
        )}

        {!ehStaff && !ehDemo && (
          <div className="sidebar-secao">
            <div className="sidebar-secao-titulo">Meus dados</div>
            <ItemMenu to="/meus-veiculos" label="Meus Veículos" />
          </div>
        )}

        {ehAdmin && (
          <div className="sidebar-secao">
            <div className="sidebar-secao-titulo">Sistema</div>
            <ItemMenu to="/usuarios" label="Usuários" contador={contagens.usuarios} />
          </div>
        )}
      </nav>

      <div className="sidebar-rodape">
        {usuario.avatarUrl ? (
          <img src={usuario.avatarUrl} alt={usuario.nome} className="avatar" />
        ) : (
          <div className="avatar" />
        )}
        <div className="sidebar-rodape-info">
          <div className="sidebar-rodape-nome">{usuario.nome}</div>
          <div className="sidebar-rodape-role">{usuario.role}</div>
        </div>
        <button className="sidebar-sair" onClick={logout}>Sair</button>
      </div>
    </aside>
  )
}
