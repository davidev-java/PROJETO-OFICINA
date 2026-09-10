import { http } from './http'

export const relatoriosApi = {
  resumo: () => http.get('/relatorios/resumo'),
  resumoDemo: () => http.get('/demo/relatorios'),
}
