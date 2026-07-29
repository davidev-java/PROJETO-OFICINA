import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { setToken, urlLoginGoogle } from '../api/http'
import { authApi } from '../api/auth'
import { useAuth } from '../context/AuthContext'

export function Login() {
  const { usuario, carregando, recarregar } = useAuth()
  const navigate = useNavigate()

  const [aba, setAba] = useState('google')
  const [modo, setModo] = useState('entrar') // 'entrar' | 'criarConta'
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  if (!carregando && usuario) {
    return <Navigate to="/" replace />
  }

  async function enviarFormulario(e) {
    e.preventDefault()
    setErro(null)
    setEnviando(true)

    try {
      const resposta = modo === 'entrar'
        ? await authApi.login(email, senha)
        : await authApi.registrar(nome, email, senha)

      setToken(resposta.token)
      await recarregar()
      navigate('/', { replace: true })
    } catch (e) {
      setErro(e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="tela-login">
      <div className="card-login">
        <h1>Oficina</h1>
        <p className="subtitulo">Sistema de gerenciamento de oficina mecânica</p>

        <div className="login-abas">
          <button type="button" className={aba === 'google' ? 'ativa' : ''} onClick={() => setAba('google')}>
            Google
          </button>
          <button type="button" className={aba === 'email' ? 'ativa' : ''} onClick={() => setAba('email')}>
            Email e senha
          </button>
        </div>

        {aba === 'google' && (
          <a className="botao-google" href={urlLoginGoogle()}>
            Entrar com Google
          </a>
        )}

        {aba === 'email' && (
          <form className="form-login" onSubmit={enviarFormulario}>
            {erro && <p className="erro">{erro}</p>}

            {modo === 'criarConta' && (
              <label>
                Nome
                <input required value={nome} onChange={(e) => setNome(e.target.value)} />
              </label>
            )}

            <label>
              Email
              <input required type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            </label>

            <label>
              Senha
              <input required type="password" minLength={6} value={senha} onChange={(e) => setSenha(e.target.value)} />
            </label>

            <button type="submit" disabled={enviando}>
              {enviando ? 'Enviando...' : modo === 'entrar' ? 'Entrar' : 'Criar conta'}
            </button>

            <div className="trocar-modo">
              {modo === 'entrar' ? (
                <>Ainda não tem conta? <button type="button" onClick={() => setModo('criarConta')}>Criar conta</button></>
              ) : (
                <>Já tem conta? <button type="button" onClick={() => setModo('entrar')}>Entrar</button></>
              )}
            </div>
          </form>
        )}

        <div className="credenciais-demo">
          Modo demo: admin_demo@oficina.demo / admin123<br />
          ou visitante@oficina.demo / visitante123
        </div>
      </div>
    </div>
  )
}
