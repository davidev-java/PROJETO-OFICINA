import { http } from './http'

export const veiculosApi = {
  listar: () => http.get('/veiculos'),
  buscarPorId: (id) => http.get(`/veiculos/${id}`),
  buscarPorCliente: (clienteId) => http.get(`/veiculos/cliente/${clienteId}`),
  criar: (dto) => http.post('/veiculos', dto),
  atualizar: (id, dto) => http.put(`/veiculos/${id}`, dto),
  deletar: (id) => http.delete(`/veiculos/${id}`),
}
