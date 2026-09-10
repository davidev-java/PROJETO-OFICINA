export function formatarMoeda(valor) {
  return Number(valor ?? 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

// Versao curta pra caber dentro do grafico: R$ 12,4 mil.
export function formatarMoedaCurta(valor) {
  const numero = Number(valor ?? 0)
  if (numero >= 1000) {
    return `R$ ${(numero / 1000).toLocaleString('pt-BR', { maximumFractionDigits: 1 })} mil`
  }
  return `R$ ${numero.toLocaleString('pt-BR', { maximumFractionDigits: 0 })}`
}

export function formatarNumero(valor) {
  return Number(valor ?? 0).toLocaleString('pt-BR')
}
