import { http } from './http'

// Espelha o /demo/** do backend: leitura publica, escrita so pra quem
// logou como admin_demo. Sempre opera sobre dados demo=true isolados.
export const demoApi = {
  listarClientes: () => http.get('/demo/clientes'),
  listarVeiculos: () => http.get('/demo/veiculos'),
  listarOrdens: () => http.get('/demo/ordem-servico'),

  criarCliente: (dto) => http.post('/demo/clientes', dto),
  deletarCliente: (id) => http.delete(`/demo/clientes/${id}`),

  criarVeiculo: (dto) => http.post('/demo/veiculos', dto),
  deletarVeiculo: (id) => http.delete(`/demo/veiculos/${id}`),

  deletarOrdemServico: (id) => http.delete(`/demo/ordem-servico/${id}`),
}
