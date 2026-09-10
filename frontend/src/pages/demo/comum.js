// Pedacinhos usados pelas quatro telas do modo demo.

export const STATUS_LABEL = {
  ABERTA: 'Aberta',
  EM_ANDAMENTO: 'Em andamento',
  FINALIZADA: 'Finalizada',
  CANCELADA: 'Cancelada',
}

// A data vem como 2026-08-14; vira 14/08/26 sem passar por Date (que
// interpretaria como UTC e podia voltar um dia).
export function formatarData(iso) {
  if (!iso) return '—'
  const [ano, mes, dia] = iso.split('-')
  return `${dia}/${mes}/${ano.slice(2)}`
}

export function formatarMoeda(valor) {
  return Number(valor ?? 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}
