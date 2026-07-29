import { http } from './http'

export const authApi = {
  login: (email, senha) => http.post('/auth/login', { email, senha }),
  registrar: (nome, email, senha) => http.post('/auth/registrar', { nome, email, senha }),
}
