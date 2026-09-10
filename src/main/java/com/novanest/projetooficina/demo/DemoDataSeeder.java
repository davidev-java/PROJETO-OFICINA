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
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

// Mantem o modo demo publico sempre com dados fresquinhos e isolados dos
// dados reais: tudo aqui e marcado com demo=true e nunca toca nas tabelas
// de producao. Roda uma vez ao subir a aplicacao e depois a cada 6h.
@Component
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder {

    public static final String EMAIL_ADMIN_DEMO = "admin_demo@oficina.demo";
    public static final String EMAIL_VISITANTE = "visitante@oficina.demo";

    // Semente fixa: o reset recria sempre a mesma oficina, com os mesmos
    // numeros - o portfolio nao pode mudar de cara a cada 6 horas.
    private static final long SEMENTE = 20260910L;
    private static final int MESES_DE_HISTORICO = 8;

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void aoIniciar() {
        garantirUsuariosDemo();

        // No boot, so popula se o banco estiver vazio. O backend roda em plano
        // free e reinicia toda vez que dorme: refazer os ~340 registros a cada
        // despertar deixava o primeiro acesso lento a toa, ja que a semente e
        // fixa e o resultado seria identico. O reset de 6h continua cuidando
        // de devolver a base ao estado original.
        if (ordemServicoRepository.countByDemo(true) > 0) {
            log.info("Modo demo ja populado - pulando a carga inicial");
            return;
        }

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
    // A base do demo e gerada em escala de oficina de verdade: dezenas de
    // clientes, uma frota inteira e varios meses de historico de OS. E o que
    // faz o relatorio ter forma - com meia duzia de registros todo grafico
    // fica reto. A semente e fixa, entao cada reset recria exatamente a mesma
    // oficina: o portfolio nao muda de cara a cada 6 horas.
    @Transactional
    public void resetarDadosDemo() {
        // Um DELETE por tabela, na ordem que respeita a chave estrangeira
        // (OS aponta pra veiculo e cliente). Apagar entidade por entidade
        // significava centenas de round-trips a cada reset.
        ordemServicoRepository.apagarTodasDemo();
        veiculoRepository.apagarTodosDemo();
        clienteRepository.apagarTodosDemo();

        Random sorteio = new Random(SEMENTE);

        List<Cliente> clientes = criarClientes(sorteio);
        List<Veiculo> veiculos = criarVeiculos(sorteio, clientes);
        int ordens = criarOrdensServico(sorteio, veiculos);

        log.info("Dados do modo demo resetados: {} clientes, {} veiculos, {} ordens de servico",
                clientes.size(), veiculos.size(), ordens);
    }

    // =========================
    // CLIENTES
    // =========================
    private List<Cliente> criarClientes(Random sorteio) {
        List<Cliente> clientes = new ArrayList<>();
        int documento = 1;

        for (int i = 0; i < CatalogoDemo.NOMES_PF.length; i++) {
            String nome = CatalogoDemo.NOMES_PF[i];
            Cliente c = new Cliente();
            c.setNome(nome);
            c.setTipoCliente(TipoCliente.PESSOA_FISICA);
            c.setCpf(GeradorDocumento.cpf(documento++));
            c.setEmail(gerarEmail(nome, i));
            c.setTelefone(String.format("(21) 9%04d-%04d", 7000 + sorteio.nextInt(2999), sorteio.nextInt(10000)));
            c.setEndereco(gerarEndereco(sorteio));
            c.setCidade(sortear(CatalogoDemo.CIDADES, sorteio));
            c.setEstado("RJ");
            c.setDataNascimento(LocalDate.now()
                    .minusYears(21 + sorteio.nextInt(40))
                    .minusDays(sorteio.nextInt(365)));
            // Cadastrado em algum momento dos ultimos 3 anos.
            c.setDataCadastro(LocalDate.now().minusDays(30 + sorteio.nextInt(1000)));
            c.setDemo(true);
            clientes.add(c);
        }

        for (int i = 0; i < CatalogoDemo.NOMES_PJ.length; i++) {
            String nome = CatalogoDemo.NOMES_PJ[i];
            Cliente c = new Cliente();
            c.setNome(nome);
            c.setTipoCliente(TipoCliente.PESSOA_JURIDICA);
            c.setCnpj(GeradorDocumento.cnpj(documento++));
            c.setEmail(gerarEmail(nome, 100 + i));
            c.setTelefone(String.format("(21) 3%03d-%04d", 100 + sorteio.nextInt(899), sorteio.nextInt(10000)));
            c.setEndereco(gerarEndereco(sorteio));
            c.setCidade(sortear(CatalogoDemo.CIDADES, sorteio));
            c.setEstado("RJ");
            c.setDataNascimento(LocalDate.now().minusYears(5 + sorteio.nextInt(20)));
            c.setDataCadastro(LocalDate.now().minusDays(200 + sorteio.nextInt(900)));
            c.setDemo(true);
            clientes.add(c);
        }

        return clienteRepository.saveAll(clientes);
    }

    // =========================
    // FROTA
    // =========================
    // Pessoa fisica costuma ter um carro (as vezes dois); empresa tem frota.
    private List<Veiculo> criarVeiculos(Random sorteio, List<Cliente> clientes) {
        List<Veiculo> veiculos = new ArrayList<>();
        Set<String> placasUsadas = new HashSet<>();

        for (Cliente cliente : clientes) {
            boolean empresa = cliente.getTipoCliente() == TipoCliente.PESSOA_JURIDICA;
            int quantidade = empresa ? 3 + sorteio.nextInt(4) : (sorteio.nextInt(10) < 3 ? 2 : 1);

            for (int i = 0; i < quantidade; i++) {
                Object[] modelo = CatalogoDemo.MODELOS[sorteio.nextInt(CatalogoDemo.MODELOS.length)];
                int anoMinimo = (int) modelo[2];
                int anoMaximo = (int) modelo[3];
                int ano = anoMinimo + sorteio.nextInt(anoMaximo - anoMinimo + 1);
                int idade = Math.max(LocalDate.now().getYear() - ano, 0);

                Veiculo v = new Veiculo();
                v.setPlaca(gerarPlaca(sorteio, placasUsadas));
                v.setMarca((String) modelo[0]);
                v.setModelo((String) modelo[1]);
                v.setAno(ano);
                v.setCor(sortear(CatalogoDemo.CORES, sorteio));
                // Frota roda mais: ~20 mil km por ano contra ~12 mil de uso pessoal.
                v.setQuilometragem(idade * (empresa ? 18000 + sorteio.nextInt(9000) : 8000 + sorteio.nextInt(8000))
                        + sorteio.nextInt(9000));
                v.setCliente(cliente);
                v.setDemo(true);
                veiculos.add(v);
            }
        }

        return veiculoRepository.saveAll(veiculos);
    }

    // =========================
    // HISTORICO DE ORDENS DE SERVICO
    // =========================
    // Mes a mes, de tras pra frente. Os meses ja fechados so tem OS finalizada
    // ou cancelada; o mes corrente e o unico com carro ainda na bancada.
    private int criarOrdensServico(Random sorteio, List<Veiculo> veiculos) {
        List<OrdemServico> ordens = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        int sequencial = 1;

        for (int volta = MESES_DE_HISTORICO - 1; volta >= 0; volta--) {
            YearMonth mes = YearMonth.from(hoje).minusMonths(volta);
            boolean mesCorrente = volta == 0;

            // Movimento crescendo devagar ao longo do periodo, com variacao.
            int quantidade = 16 + (MESES_DE_HISTORICO - volta) * 2 + sorteio.nextInt(7);

            for (int i = 0; i < quantidade; i++) {
                LocalDate abertura = diaDentroDoMes(mes, hoje, sorteio);
                StatusOS status = sortearStatus(mesCorrente, sorteio);

                LocalDate fechamento = null;
                if (status == StatusOS.FINALIZADA) {
                    fechamento = abertura.plusDays(1 + sorteio.nextInt(8));
                    if (fechamento.isAfter(hoje)) {
                        fechamento = hoje;
                    }
                }

                Object[] servico = CatalogoDemo.SERVICOS[sorteio.nextInt(CatalogoDemo.SERVICOS.length)];
                BigDecimal maoDeObra = valorNaFaixa((int) servico[1], (int) servico[2], sorteio);
                BigDecimal pecas = valorNaFaixa((int) servico[3], (int) servico[4], sorteio);
                BigDecimal desconto = sortearDesconto(maoDeObra.add(pecas), sorteio);

                Veiculo veiculo = veiculos.get(sorteio.nextInt(veiculos.size()));

                OrdemServico os = new OrdemServico();
                os.setNumeroOs(String.format("OS-%d-%04d", mes.getYear(), sequencial++));
                os.setCliente(veiculo.getCliente());
                os.setVeiculo(veiculo);
                os.setStatus(status);
                os.setDescricaoProblema((String) servico[0]);
                os.setDataAbertura(abertura);
                os.setDataFechamento(fechamento);
                os.setValorMaoDeObra(maoDeObra);
                os.setValorPecas(pecas);
                os.setDesconto(desconto);
                os.setValorTotal(maoDeObra.add(pecas).subtract(desconto));
                os.setDemo(true);
                ordens.add(os);
            }
        }

        ordemServicoRepository.saveAll(ordens);
        return ordens.size();
    }

    // Mes fechado: qualquer dia. Mes corrente: nada depois de hoje.
    private LocalDate diaDentroDoMes(YearMonth mes, LocalDate hoje, Random sorteio) {
        int ultimoDia = YearMonth.from(hoje).equals(mes) ? hoje.getDayOfMonth() : mes.lengthOfMonth();
        return mes.atDay(1 + sorteio.nextInt(ultimoDia));
    }

    private StatusOS sortearStatus(boolean mesCorrente, Random sorteio) {
        int sorte = sorteio.nextInt(100);

        if (!mesCorrente) {
            return sorte < 93 ? StatusOS.FINALIZADA : StatusOS.CANCELADA;
        }

        if (sorte < 40) return StatusOS.FINALIZADA;
        if (sorte < 70) return StatusOS.EM_ANDAMENTO;
        if (sorte < 96) return StatusOS.ABERTA;
        return StatusOS.CANCELADA;
    }

    // Oficina cobra em valor redondo, nao em centavo quebrado.
    private BigDecimal valorNaFaixa(int minimo, int maximo, Random sorteio) {
        int valor = maximo <= minimo ? minimo : minimo + sorteio.nextInt(maximo - minimo + 1);
        return BigDecimal.valueOf(valor - (valor % 10)).setScale(2, RoundingMode.HALF_UP);
    }

    // Desconto so em parte das OS, e sempre um percentual do total.
    private BigDecimal sortearDesconto(BigDecimal bruto, Random sorteio) {
        if (sorteio.nextInt(100) >= 35) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        int percentual = 5 + sorteio.nextInt(8);
        return bruto.multiply(BigDecimal.valueOf(percentual))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Placa no padrao Mercosul (LLLNLNN), unica dentro da frota do demo.
    private String gerarPlaca(Random sorteio, Set<String> usadas) {
        String placa;
        do {
            placa = "" + letra(sorteio) + letra(sorteio) + letra(sorteio)
                    + sorteio.nextInt(10) + letra(sorteio)
                    + sorteio.nextInt(10) + sorteio.nextInt(10);
        } while (!usadas.add(placa));
        return placa;
    }

    private char letra(Random sorteio) {
        return (char) ('A' + sorteio.nextInt(26));
    }

    private String gerarEndereco(Random sorteio) {
        return sortear(CatalogoDemo.LOGRADOUROS, sorteio) + ", " + (10 + sorteio.nextInt(1990));
    }

    // Email so com letra e ponto: nada de acento ou espaco escapando pro banco.
    private String gerarEmail(String nome, int indice) {
        String base = Normalizer.normalize(nome.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z ]", "")
                .trim()
                .replace(' ', '.');
        return base + "." + indice + "@oficina.demo";
    }

    private String sortear(String[] opcoes, Random sorteio) {
        return opcoes[sorteio.nextInt(opcoes.length)];
    }
}
