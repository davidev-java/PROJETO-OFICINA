import { http } from './http'

export const clientesApi = {
  listar: () => http.get('/clientes'),
  buscarPorId: (id) => http.get(`/clientes/${id}`),
  criar: (dto) => http.post('/clientes', dto),
  atualizar: (id, dto) => http.put(`/clientes/${id}`, dto),
  deletar: (id) => http.delete(`/clientes/${id}`),
  buscarPorNome: (nome) => http.get(`/clientes/nome?nome=${encodeURIComponent(nome)}`),
}
