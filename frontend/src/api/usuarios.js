import { http } from './http'

export const usuariosApi = {
  me: () => http.get('/usuarios/me'),
  listar: () => http.get('/usuarios'),
  atualizarRole: (id, role) => http.patch(`/usuarios/${id}/role?role=${role}`),
}
