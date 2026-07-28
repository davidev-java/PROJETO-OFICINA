import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { clientesApi } from '../../api/clientes'

const VAZIO = {
  nome: '',
  tipoCliente: 'PESSOA_FISICA',
  cpf: '',
  cnpj: '',
  telefone: '',
  email: '',
  endereco: '',
  dataNascimento: '',
  observacoes: '',
  whatsapp: '',
  cidade: '',
  estado: '',
}

export function ClienteForm() {
  const { id } = useParams()
  const editando = Boolean(id)
  const navigate = useNavigate()

  const [dados, setDados] = useState(VAZIO)
  const [erro, setErro] = useState(null)
  const [salvando, setSalvando] = useState(false)

  useEffect(() => {
    if (!editando) return
    clientesApi.buscarPorId(id).then((c) => setDados({ ...VAZIO, ...c }))
  }, [id, editando])

  function alterar(campo, valor) {
    setDados((atual) => ({ ...atual, [campo]: valor }))
  }

  async function salvar(e) {
    e.preventDefault()
    setErro(null)
    setSalvando(true)

    try {
      if (editando) {
        await clientesApi.atualizar(id, dados)
      } else {
        await clientesApi.criar(dados)
      }
      navigate('/clientes')
    } catch (e) {
      setErro(e.message)
    } finally {
      setSalvando(false)
    }
  }

  return (
    <div className="pagina">
      <h1>{editando ? 'Editar cliente' : 'Novo cliente'}</h1>

      <form className="formulario" onSubmit={salvar}>
        {erro && <p className="erro">{erro}</p>}

        <label>
          Nome
          <input required value={dados.nome} onChange={(e) => alterar('nome', e.target.value)} />
        </label>

        <label>
          Tipo de cliente
          <select value={dados.tipoCliente} onChange={(e) => alterar('tipoCliente', e.target.value)}>
            <option value="PESSOA_FISICA">Pessoa Física</option>
            <option value="PESSOA_JURIDICA">Pessoa Jurídica</option>
          </select>
        </label>

        <div className="linha">
          <label>
            CPF
            <input value={dados.cpf ?? ''} onChange={(e) => alterar('cpf', e.target.value)} />
          </label>
          <label>
            CNPJ
            <input value={dados.cnpj ?? ''} onChange={(e) => alterar('cnpj', e.target.value)} />
          </label>
        </div>

        <div className="linha">
          <label>
            Telefone
            <input required value={dados.telefone} onChange={(e) => alterar('telefone', e.target.value)} />
          </label>
          <label>
            WhatsApp
            <input value={dados.whatsapp ?? ''} onChange={(e) => alterar('whatsapp', e.target.value)} />
          </label>
        </div>

        <label>
          Email
          <input type="email" required value={dados.email} onChange={(e) => alterar('email', e.target.value)} />
        </label>

        <label>
          Endereço
          <input required value={dados.endereco} onChange={(e) => alterar('endereco', e.target.value)} />
        </label>

        <div className="linha">
          <label>
            Cidade
            <input value={dados.cidade ?? ''} onChange={(e) => alterar('cidade', e.target.value)} />
          </label>
          <label>
            Estado
            <input maxLength={2} value={dados.estado ?? ''} onChange={(e) => alterar('estado', e.target.value.toUpperCase())} />
          </label>
        </div>

        <label>
          Data de nascimento
          <input type="date" required value={dados.dataNascimento ?? ''} onChange={(e) => alterar('dataNascimento', e.target.value)} />
        </label>

        <label>
          Observações
          <textarea value={dados.observacoes ?? ''} onChange={(e) => alterar('observacoes', e.target.value)} />
        </label>

        <div className="acoes-formulario">
          <button type="button" onClick={() => navigate('/clientes')}>Cancelar</button>
          <button type="submit" disabled={salvando}>{salvando ? 'Salvando...' : 'Salvar'}</button>
        </div>
      </form>
    </div>
  )
}
