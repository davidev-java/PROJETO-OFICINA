import { useEffect, useState } from 'react'
import { usuariosApi } from '../../api/usuarios'

const ROLES = ['ADMIN', 'SUPERVISOR', 'MECANICO', 'ATENDENTE', 'CLIENTE']

export function UsuariosPage() {
  const [usuarios, setUsuarios] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  function carregar() {
    setCarregando(true)
    usuariosApi.listar()
      .then(setUsuarios)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }

  useEffect(carregar, [])

  async function mudarRole(id, novaRole) {
    try {
      await usuariosApi.atualizarRole(id, novaRole)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <div>
      <div className="pagina-topo">
        <h1>Usuários</h1>
      </div>

      {erro && <p className="erro">{erro}</p>}
      {carregando && <p>Carregando...</p>}

      {!carregando && (
        <div className="tabela-wrap">
          <table className="tabela">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Email</th>
                <th>Role</th>
                <th>Origem</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((u) => (
                <tr key={u.id}>
                  <td>{u.nome}</td>
                  <td className="mono">{u.email}</td>
                  <td>
                    <select value={u.role} onChange={(e) => mudarRole(u.id, e.target.value)}>
                      {ROLES.map((r) => <option key={r} value={r}>{r}</option>)}
                    </select>
                  </td>
                  <td>{u.demo ? 'Demo' : 'Real'}</td>
                </tr>
              ))}
              {usuarios.length === 0 && (
                <tr><td colSpan={4}>Nenhum usuário encontrado.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
