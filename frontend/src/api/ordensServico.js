import { http } from './http'

export const ordensServicoApi = {
  listar: () => http.get('/ordem-servico'),
  minhas: () => http.get('/ordem-servico/minhas'),
  buscarPorId: (id) => http.get(`/ordem-servico/${id}`),
  criar: (dto) => http.post('/ordem-servico', dto),
  atualizar: (id, dto) => http.put(`/ordem-servico/${id}`, dto),
  finalizar: (id) => http.post(`/ordem-servico/${id}/finalizar`),
  cancelar: (id) => http.post(`/ordem-servico/${id}/cancelar`),
  deletar: (id) => http.delete(`/ordem-servico/${id}`),
}
