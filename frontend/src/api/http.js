const API_URL = import.meta.env.VITE_API_URL

export function getToken() {
  return localStorage.getItem('token')
}

export function setToken(token) {
  localStorage.setItem('token', token)
}

export function limparToken() {
  localStorage.removeItem('token')
}

export function urlLoginGoogle() {
  return `${API_URL}/oauth2/authorization/google`
}

async function request(path, options = {}) {
  const token = getToken()

  const headers = {
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  }

  const response = await fetch(`${API_URL}${path}`, { ...options, headers })

  if (response.status === 401) {
    limparToken()
    window.location.href = '/login'
    throw new Error('Sessão expirada, faça login novamente')
  }

  if (response.status === 204) {
    return null
  }

  const texto = await response.text()
  const dados = texto ? JSON.parse(texto) : null

  if (!response.ok) {
    const mensagem = dados?.erro || 'Erro inesperado ao falar com o servidor'
    throw new Error(mensagem)
  }

  return dados
}

export const http = {
  get: (path) => request(path),
  post: (path, body) => request(path, { method: 'POST', body: JSON.stringify(body) }),
  put: (path, body) => request(path, { method: 'PUT', body: JSON.stringify(body) }),
  patch: (path, body) => request(path, { method: 'PATCH', body: body ? JSON.stringify(body) : undefined }),
  delete: (path) => request(path, { method: 'DELETE' }),
}
