import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { veiculosApi } from '../../api/veiculos'
import { clientesApi } from '../../api/clientes'

const VAZIO = {
  placa: '',
  marca: '',
  modelo: '',
  ano: '',
  cor: '',
  quilometragem: '',
  clienteId: '',
}

export function VeiculoForm() {
  const { id } = useParams()
  const editando = Boolean(id)
  const navigate = useNavigate()

  const [dados, setDados] = useState(VAZIO)
  const [clientes, setClientes] = useState([])
  const [erro, setErro] = useState(null)
  const [salvando, setSalvando] = useState(false)

  useEffect(() => {
    clientesApi.listar().then(setClientes).catch((e) => setErro(e.message))
  }, [])

  useEffect(() => {
    if (!editando) return
    veiculosApi.buscarPorId(id).then((v) => setDados({ ...VAZIO, ...v }))
  }, [id, editando])

  function alterar(campo, valor) {
    setDados((atual) => ({ ...atual, [campo]: valor }))
  }

  async function salvar(e) {
    e.preventDefault()
    setErro(null)
    setSalvando(true)

    const payload = {
      ...dados,
      ano: dados.ano ? Number(dados.ano) : null,
      quilometragem: dados.quilometragem ? Number(dados.quilometragem) : null,
    }

    try {
      if (editando) {
        await veiculosApi.atualizar(id, payload)
      } else {
        await veiculosApi.criar(payload)
      }
      navigate('/veiculos')
    } catch (e) {
      setErro(e.message)
    } finally {
      setSalvando(false)
    }
  }

  return (
    <div className="pagina">
      <h1>{editando ? 'Editar veículo' : 'Novo veículo'}</h1>

      <form className="formulario" onSubmit={salvar}>
        {erro && <p className="erro">{erro}</p>}

        <label>
          Cliente
          <select required value={dados.clienteId ?? ''} onChange={(e) => alterar('clienteId', e.target.value)}>
            <option value="" disabled>Selecione um cliente</option>
            {clientes.map((c) => (
              <option key={c.id} value={c.id}>{c.nome}</option>
            ))}
          </select>
        </label>

        <div className="linha">
          <label>
            Placa
            <input required maxLength={7} value={dados.placa} onChange={(e) => alterar('placa', e.target.value.toUpperCase())} />
          </label>
          <label>
            Ano
            <input required type="number" value={dados.ano ?? ''} onChange={(e) => alterar('ano', e.target.value)} />
          </label>
        </div>

        <div className="linha">
          <label>
            Marca
            <input required value={dados.marca} onChange={(e) => alterar('marca', e.target.value)} />
          </label>
          <label>
            Modelo
            <input required value={dados.modelo} onChange={(e) => alterar('modelo', e.target.value)} />
          </label>
        </div>

        <div className="linha">
          <label>
            Cor
            <input required value={dados.cor} onChange={(e) => alterar('cor', e.target.value)} />
          </label>
          <label>
            Quilometragem
            <input required type="number" value={dados.quilometragem ?? ''} onChange={(e) => alterar('quilometragem', e.target.value)} />
          </label>
        </div>

        <div className="acoes-formulario">
          <button type="button" onClick={() => navigate('/veiculos')}>Cancelar</button>
          <button type="submit" disabled={salvando}>{salvando ? 'Salvando...' : 'Salvar'}</button>
        </div>
      </form>
    </div>
  )
}
