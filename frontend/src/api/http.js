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

// ===== ESTADO DO SERVIDOR (COLD START) =====
// O backend roda em plano free: depois de um tempo parado ele dorme e a
// primeira requisicao demora ~1min. Quem avisa a interface disso e este
// pequeno pub/sub: qualquer requisicao que passe de 1,5s liga o overlay.

let ouvintes = []
let requisicoesAbertas = 0
let timerOverlay = null
let acordando = false

export function assinarStatusServidor(ouvinte) {
  ouvintes.push(ouvinte)
  ouvinte(acordando)
  return () => {
    ouvintes = ouvintes.filter((o) => o !== ouvinte)
  }
}

function notificar(valor) {
  if (acordando === valor) return
  acordando = valor
  ouvintes.forEach((o) => o(acordando))
}

function abriuRequisicao() {
  requisicoesAbertas++
  if (!timerOverlay) {
    timerOverlay = setTimeout(() => notificar(true), 1500)
  }
}

function fechouRequisicao() {
  requisicoesAbertas = Math.max(0, requisicoesAbertas - 1)
  if (requisicoesAbertas === 0) {
    clearTimeout(timerOverlay)
    timerOverlay = null
    notificar(false)
  }
}

// Acorda o backend batendo em /health ate ele responder. Usado antes de
// qualquer navegacao que sairia do nosso dominio (login Google), pra que o
// visitante nunca veja a pagina de "starting" da hospedagem.
export async function despertarBackend({ tempoLimiteMs = 120000 } = {}) {
  const limite = Date.now() + tempoLimiteMs

  while (Date.now() < limite) {
    try {
      const resposta = await fetch(`${API_URL}/health`, { cache: 'no-store' })
      if (resposta.ok) return true
    } catch {
      // servidor ainda dormindo - tenta de novo
    }
    await new Promise((resolve) => setTimeout(resolve, 2000))
  }

  return false
}

// Aquece o backend em segundo plano assim que o site abre, sem overlay:
// quando o visitante clicar em alguma coisa, ele ja acordou.
export function aquecerBackend() {
  fetch(`${API_URL}/health`, { cache: 'no-store' }).catch(() => {})
}

// Leva o visitante pro login do Google so depois do backend estar de pe,
// com o overlay na frente enquanto isso.
export async function entrarComGoogle() {
  notificar(true)
  await despertarBackend()
  window.location.href = urlLoginGoogle()
}

async function request(path, options = {}) {
  const token = getToken()

  const headers = {
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  }

  abriuRequisicao()

  let response
  try {
    response = await fetch(`${API_URL}${path}`, { ...options, headers })
  } finally {
    fechouRequisicao()
  }

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
