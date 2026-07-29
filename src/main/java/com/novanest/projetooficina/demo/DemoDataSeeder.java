package com.novanest.projetooficina.demo;

import com.novanest.projetooficina.entity.Cliente;
import com.novanest.projetooficina.entity.OrdemServico;
import com.novanest.projetooficina.entity.Usuario;
import com.novanest.projetooficina.entity.Veiculo;
import com.novanest.projetooficina.enums.Role;
import com.novanest.projetooficina.enums.StatusOS;
import com.novanest.projetooficina.enums.TipoCliente;
import com.novanest.projetooficina.repository.ClienteRepository;
import com.novanest.projetooficina.repository.OrdemServicoRepository;
import com.novanest.projetooficina.repository.UsuarioRepository;
import com.novanest.projetooficina.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

// Mantem o modo demo publico sempre com dados fresquinhos e isolados dos
// dados reais: tudo aqui e marcado com demo=true e nunca toca nas tabelas
// de producao. Roda uma vez ao subir a aplicacao e depois a cada 6h.
@Component
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder {

    public static final String EMAIL_ADMIN_DEMO = "admin_demo@oficina.demo";
    public static final String EMAIL_VISITANTE = "visitante@oficina.demo";

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void aoIniciar() {
        garantirUsuariosDemo();
        resetarDadosDemo();
    }

    // A cada 6h (21600000 ms) - "a cada X horas" pedido no requisito do modo demo.
    // initialDelay evita rodar de novo logo no startup (o ApplicationReadyEvent ja cuida disso).
    @Scheduled(fixedRate = 21_600_000, initialDelay = 21_600_000)
    public void resetPeriodico() {
        resetarDadosDemo();
    }

    // =========================
    // USUARIOS FIXOS DE DEMONSTRACAO
    // =========================
    private void garantirUsuariosDemo() {
        usuarioRepository.findByEmail(EMAIL_ADMIN_DEMO).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setEmail(EMAIL_ADMIN_DEMO);
            u.setNome("Admin Demo");
            u.setSenha(passwordEncoder.encode("admin123"));
            u.setRole(Role.ADMIN);
            u.setDemo(true);
            return usuarioRepository.save(u);
        });

        usuarioRepository.findByEmail(EMAIL_VISITANTE).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setEmail(EMAIL_VISITANTE);
            u.setNome("Visitante");
            u.setSenha(passwordEncoder.encode("visitante123"));
            u.setRole(Role.CLIENTE);
            u.setDemo(true);
            return usuarioRepository.save(u);
        });
    }

    // =========================
    // RESETAR DADOS DEMO PARA O ESTADO INICIAL
    // =========================
    @Transactional
    public void resetarDadosDemo() {
        ordemServicoRepository.deleteAll(ordemServicoRepository.findByDemoTrue());
        veiculoRepository.deleteAll(veiculoRepository.findByDemoTrue());
        clienteRepository.deleteAll(clienteRepository.findByDemoTrue());

        Cliente ana = salvarCliente("Ana Ferreira Souza", TipoCliente.PESSOA_FISICA,
                GeradorDocumento.cpf(1), null, "ana.demo@exemplo.com", "(21) 98888-0001",
                "Rua das Palmeiras, 120", "Rio de Janeiro", "RJ", LocalDate.of(1990, 4, 12));

        Cliente bruno = salvarCliente("Bruno Martins Lima", TipoCliente.PESSOA_FISICA,
                GeradorDocumento.cpf(2), null, "bruno.demo@exemplo.com", "(21) 98888-0002",
                "Av. Atlântica, 900", "Rio de Janeiro", "RJ", LocalDate.of(1985, 9, 3));

        Cliente transportesRio = salvarCliente("Transportes Rio LTDA", TipoCliente.PESSOA_JURIDICA,
                null, GeradorDocumento.cnpj(1), "contato.demo@transportesrio.com", "(21) 3333-0003",
                "Rod. Presidente Dutra, km 200", "Duque de Caxias", "RJ", LocalDate.of(2010, 1, 20));

        Cliente carla = salvarCliente("Carla Mendes Rocha", TipoCliente.PESSOA_FISICA,
                GeradorDocumento.cpf(3), null, "carla.demo@exemplo.com", "(21) 98888-0004",
                "Rua Voluntários da Pátria, 55", "Rio de Janeiro", "RJ", LocalDate.of(1993, 7, 28));

        Veiculo argo = salvarVeiculo("DEM0A01", "Fiat", "Argo", 2021, "Prata", 32000, ana);
        Veiculo civic = salvarVeiculo("DEM0A02", "Honda", "Civic", 2019, "Preto", 58000, ana);
        Veiculo gol = salvarVeiculo("DEM0A03", "Volkswagen", "Gol", 2018, "Branco", 71000, bruno);
        Veiculo master = salvarVeiculo("DEM0A04", "Renault", "Master", 2020, "Branco", 95000, transportesRio);
        Veiculo corolla = salvarVeiculo("DEM0A05", "Toyota", "Corolla", 2022, "Cinza", 18000, carla);
        Veiculo compass = salvarVeiculo("DEM0A06", "Jeep", "Compass", 2020, "Vermelho", 40000, transportesRio);

        salvarOrdemServico(ana, argo, StatusOS.ABERTA,
                "Troca de óleo e filtros", "600", "180", "0", null);

        salvarOrdemServico(ana, civic, StatusOS.EM_ANDAMENTO,
                "Barulho na suspensão dianteira", "350", "420", "50", null);

        salvarOrdemServico(bruno, gol, StatusOS.FINALIZADA,
                "Revisão completa dos 70.000 km", "480", "610", "100", LocalDate.now().minusDays(2));

        salvarOrdemServico(transportesRio, master, StatusOS.CANCELADA,
                "Troca de embreagem", "900", "1200", "0", null);

        salvarOrdemServico(carla, corolla, StatusOS.ABERTA,
                "Ar condicionado não gela", "150", "300", "0", null);

        log.info("Dados do modo demo resetados com sucesso");
    }

    private Cliente salvarCliente(String nome, TipoCliente tipo, String cpf, String cnpj,
                                   String email, String telefone, String endereco,
                                   String cidade, String estado, LocalDate nascimento) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setTipoCliente(tipo);
        c.setCpf(cpf);
        c.setCnpj(cnpj);
        c.setEmail(email);
        c.setTelefone(telefone);
        c.setEndereco(endereco);
        c.setCidade(cidade);
        c.setEstado(estado);
        c.setDataNascimento(nascimento);
        c.setDemo(true);
        return clienteRepository.save(c);
    }

    private Veiculo salvarVeiculo(String placa, String marca, String modelo, int ano,
                                   String cor, int km, Cliente cliente) {
        Veiculo v = new Veiculo();
        v.setPlaca(placa);
        v.setMarca(marca);
        v.setModelo(modelo);
        v.setAno(ano);
        v.setCor(cor);
        v.setQuilometragem(km);
        v.setCliente(cliente);
        v.setDemo(true);
        return veiculoRepository.save(v);
    }

    private void salvarOrdemServico(Cliente cliente, Veiculo veiculo, StatusOS status,
                                     String descricao, String maoDeObra, String pecas,
                                     String desconto, LocalDate fechamento) {
        OrdemServico os = new OrdemServico();
        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        os.setStatus(status);
        os.setDescricaoProblema(descricao);
        os.setValorMaoDeObra(new BigDecimal(maoDeObra));
        os.setValorPecas(new BigDecimal(pecas));
        os.setDesconto(new BigDecimal(desconto));
        os.setValorTotal(os.getValorMaoDeObra().add(os.getValorPecas()).subtract(os.getDesconto()));
        os.setDataFechamento(fechamento);
        os.setDemo(true);
        ordemServicoRepository.save(os);
    }
}
