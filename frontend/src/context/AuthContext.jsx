import { createContext, useContext, useEffect, useState } from 'react'
import { getToken, limparToken } from '../api/http'
import { usuariosApi } from '../api/usuarios'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [carregando, setCarregando] = useState(true)

  async function carregarUsuario() {
    if (!getToken()) {
      setUsuario(null)
      setCarregando(false)
      return
    }

    try {
      const dados = await usuariosApi.me()
      setUsuario(dados)
    } catch {
      setUsuario(null)
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => {
    carregarUsuario()
  }, [])

  function logout() {
    limparToken()
    setUsuario(null)
    window.location.href = '/login'
  }

  return (
    <AuthContext.Provider value={{ usuario, carregando, logout, recarregar: carregarUsuario }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
