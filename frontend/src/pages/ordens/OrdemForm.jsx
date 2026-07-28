import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { ordensServicoApi } from '../../api/ordensServico'
import { clientesApi } from '../../api/clientes'
import { veiculosApi } from '../../api/veiculos'

const VAZIO = {
  clienteId: '',
  veiculoId: '',
  statusOS: 'ABERTA',
  descricaoProblema: '',
  valorMaoDeObra: '0',
  valorPecas: '0',
  desconto: '0',
}

export function OrdemForm() {
  const { id } = useParams()
  const editando = Boolean(id)
  const navigate = useNavigate()

  const [dados, setDados] = useState(VAZIO)
  const [clientes, setClientes] = useState([])
  const [veiculos, setVeiculos] = useState([])
  const [erro, setErro] = useState(null)
  const [salvando, setSalvando] = useState(false)

  useEffect(() => {
    clientesApi.listar().then(setClientes).catch((e) => setErro(e.message))
  }, [])

  useEffect(() => {
    if (!editando) return
    ordensServicoApi.buscarPorId(id).then((o) =>
      setDados({
        clienteId: o.cliente?.id ?? '',
        veiculoId: o.veiculo?.id ?? '',
        statusOS: o.status,
        descricaoProblema: o.descricaoProblema,
        valorMaoDeObra: String(o.valorMaoDeObra ?? 0),
        valorPecas: String(o.valorPecas ?? 0),
        desconto: String(o.desconto ?? 0),
      }),
    )
  }, [id, editando])

  useEffect(() => {
    if (!dados.clienteId) {
      setVeiculos([])
      return
    }
    veiculosApi.buscarPorCliente(dados.clienteId).then(setVeiculos).catch(() => setVeiculos([]))
  }, [dados.clienteId])

  function alterar(campo, valor) {
    setDados((atual) => ({ ...atual, [campo]: valor }))
  }

  async function salvar(e) {
    e.preventDefault()
    setErro(null)
    setSalvando(true)

    const payload = {
      ...dados,
      valorMaoDeObra: Number(dados.valorMaoDeObra),
      valorPecas: Number(dados.valorPecas),
      desconto: Number(dados.desconto),
    }

    try {
      if (editando) {
        await ordensServicoApi.atualizar(id, payload)
      } else {
        await ordensServicoApi.criar(payload)
      }
      navigate('/ordens')
    } catch (e) {
      setErro(e.message)
    } finally {
      setSalvando(false)
    }
  }

  return (
    <div className="pagina">
      <h1>{editando ? 'Editar ordem de serviço' : 'Nova ordem de serviço'}</h1>

      <form className="formulario" onSubmit={salvar}>
        {erro && <p className="erro">{erro}</p>}

        <label>
          Cliente
          <select required value={dados.clienteId} onChange={(e) => alterar('clienteId', e.target.value)}>
            <option value="" disabled>Selecione um cliente</option>
            {clientes.map((c) => (
              <option key={c.id} value={c.id}>{c.nome}</option>
            ))}
          </select>
        </label>

        <label>
          Veículo
          <select required value={dados.veiculoId} onChange={(e) => alterar('veiculoId', e.target.value)} disabled={!dados.clienteId}>
            <option value="" disabled>Selecione um veículo</option>
            {veiculos.map((v) => (
              <option key={v.id} value={v.id}>{v.placa} - {v.marca} {v.modelo}</option>
            ))}
          </select>
        </label>

        {editando && (
          <label>
            Status
            <select value={dados.statusOS} onChange={(e) => alterar('statusOS', e.target.value)}>
              <option value="ABERTA">Aberta</option>
              <option value="EM_ANDAMENTO">Em andamento</option>
              <option value="FINALIZADA">Finalizada</option>
              <option value="CANCELADA">Cancelada</option>
            </select>
          </label>
        )}

        <label>
          Descrição do problema
          <textarea required maxLength={500} value={dados.descricaoProblema} onChange={(e) => alterar('descricaoProblema', e.target.value)} />
        </label>

        <div className="linha">
          <label>
            Valor mão de obra (R$)
            <input required type="number" step="0.01" min="0" value={dados.valorMaoDeObra} onChange={(e) => alterar('valorMaoDeObra', e.target.value)} />
          </label>
          <label>
            Valor peças (R$)
            <input required type="number" step="0.01" min="0" value={dados.valorPecas} onChange={(e) => alterar('valorPecas', e.target.value)} />
          </label>
          <label>
            Desconto (R$)
            <input required type="number" step="0.01" min="0" value={dados.desconto} onChange={(e) => alterar('desconto', e.target.value)} />
          </label>
        </div>

        <div className="acoes-formulario">
          <button type="button" onClick={() => navigate('/ordens')}>Cancelar</button>
          <button type="submit" disabled={salvando}>{salvando ? 'Salvando...' : 'Salvar'}</button>
        </div>
      </form>
    </div>
  )
}
