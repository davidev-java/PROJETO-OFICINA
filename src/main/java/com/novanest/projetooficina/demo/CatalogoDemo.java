package com.novanest.projetooficina.demo;

// Massa de dados do modo demo. Fica separado do seeder pra que a logica de
// geracao (DemoDataSeeder) nao se perca no meio de listas gigantes.
final class CatalogoDemo {

    private CatalogoDemo() {
    }

    // ===== PESSOAS FISICAS =====
    static final String[] NOMES_PF = {
            "Ana Ferreira Souza", "Bruno Martins Lima", "Carla Mendes Rocha", "Diego Alves Pinto",
            "Eduarda Nascimento Silva", "Felipe Barbosa Cardoso", "Gabriela Ramos Teixeira",
            "Henrique Moraes Dias", "Isabela Correia Fontes", "João Vitor Almeida", "Karina Duarte Peixoto",
            "Leonardo Figueiredo Braga", "Mariana Castro Lopes", "Nathalia Freitas Azevedo",
            "Otávio Siqueira Monteiro", "Patrícia Gomes Vieira", "Rafael Andrade Coutinho",
            "Sabrina Oliveira Pires", "Thiago Nogueira Campos", "Vanessa Ribeiro Sales",
            "William Prado Cunha", "Yasmin Batista Moreira", "André Luiz Sampaio", "Beatriz Carvalho Reis",
            "Caio Henrique Bastos", "Daniela Marques Antunes", "Elisa Tavares Guedes",
            "Fábio Junqueira Neves", "Giovana Paiva Brandão", "Hugo Serrano Vasques",
            "Ingrid Amaral Bezerra", "Juliano Esteves Rangel", "Larissa Fonseca Trindade",
            "Marcelo Aguiar Bittencourt", "Natália Queiroz Salgado", "Paulo Ricardo Furtado"
    };

    // ===== EMPRESAS (frota) =====
    static final String[] NOMES_PJ = {
            "Transportes Rio LTDA", "Frota Nova Logística ME", "Express Entregas Rápidas LTDA",
            "Construtora Baía Azul LTDA", "Distribuidora Serra Verde ME", "Locadora Costa Sul LTDA",
            "Alimentos Boa Praça LTDA", "Serviços Urbanos Maré LTDA"
    };

    static final String[] CIDADES = {
            "Rio de Janeiro", "Niterói", "Duque de Caxias", "São Gonçalo", "Nova Iguaçu",
            "Petrópolis", "Volta Redonda", "Macaé", "Cabo Frio", "Campos dos Goytacazes"
    };

    static final String[] LOGRADOUROS = {
            "Rua das Palmeiras", "Av. Atlântica", "Rua Voluntários da Pátria", "Av. Brasil",
            "Rua Maxwell", "Estrada do Galeão", "Av. Nossa Senhora de Copacabana", "Rua Dias da Cruz",
            "Av. Rio Branco", "Rua Barão de Mesquita", "Estrada dos Bandeirantes", "Av. das Américas"
    };

    // ===== FROTA: marca, modelo, ano minimo, ano maximo =====
    static final Object[][] MODELOS = {
            {"Fiat", "Argo", 2018, 2024}, {"Fiat", "Strada", 2017, 2024}, {"Fiat", "Mobi", 2017, 2023},
            {"Fiat", "Toro", 2018, 2024}, {"Volkswagen", "Gol", 2015, 2022}, {"Volkswagen", "Polo", 2018, 2024},
            {"Volkswagen", "T-Cross", 2019, 2024}, {"Volkswagen", "Saveiro", 2016, 2023},
            {"Chevrolet", "Onix", 2016, 2024}, {"Chevrolet", "Tracker", 2018, 2024},
            {"Chevrolet", "S10", 2015, 2023}, {"Hyundai", "HB20", 2016, 2024},
            {"Hyundai", "Creta", 2018, 2024}, {"Toyota", "Corolla", 2017, 2024},
            {"Toyota", "Hilux", 2016, 2023}, {"Toyota", "Yaris", 2018, 2023},
            {"Honda", "Civic", 2015, 2022}, {"Honda", "Fit", 2015, 2021}, {"Honda", "HR-V", 2017, 2024},
            {"Renault", "Kwid", 2018, 2024}, {"Renault", "Master", 2017, 2023},
            {"Renault", "Duster", 2016, 2023}, {"Jeep", "Renegade", 2017, 2024},
            {"Jeep", "Compass", 2018, 2024}, {"Nissan", "Kicks", 2018, 2023},
            {"Peugeot", "208", 2017, 2023}, {"Citroën", "C3", 2016, 2023}, {"Ford", "Ka", 2015, 2021},
            {"Ford", "Ranger", 2016, 2023}
    };

    static final String[] CORES = {
            "Branco", "Prata", "Preto", "Cinza", "Vermelho", "Azul", "Grafite", "Bege"
    };

    // ===== SERVICOS: descricao, mao de obra min/max, pecas min/max =====
    // As faixas sao o que faz o faturamento parecer de oficina de verdade:
    // troca de oleo convive com retifica de motor.
    static final Object[][] SERVICOS = {
            {"Troca de óleo e filtro de óleo", 120, 190, 180, 340},
            {"Alinhamento, balanceamento e cambagem", 90, 160, 0, 80},
            {"Troca de pastilhas e discos de freio dianteiros", 220, 340, 380, 780},
            {"Revisão preventiva completa", 380, 580, 420, 940},
            {"Troca da correia dentada, tensor e bomba d'água", 480, 760, 520, 1020},
            {"Reparo no sistema de arrefecimento (radiador)", 280, 440, 300, 680},
            {"Substituição do kit de embreagem", 780, 1180, 950, 1850},
            {"Recarga e higienização do ar-condicionado", 160, 250, 120, 300},
            {"Troca dos amortecedores dianteiros e batentes", 300, 480, 640, 1290},
            {"Diagnóstico eletrônico e reparo na injeção", 240, 400, 180, 920},
            {"Troca de bateria e teste do alternador", 60, 110, 420, 820},
            {"Retífica parcial do motor", 1900, 2900, 2400, 4600},
            {"Troca da bomba de combustível", 320, 500, 540, 1010},
            {"Substituição do escapamento e catalisador", 180, 280, 380, 760},
            {"Revisão do sistema de freios com sangria", 180, 280, 120, 400},
            {"Troca do kit de distribuição e velas", 260, 420, 340, 720},
            {"Reparo elétrico: vidro e trava elétrica", 140, 240, 160, 420},
            {"Troca de suspensão traseira completa", 420, 680, 720, 1480},
            {"Higienização e troca do filtro de cabine", 80, 140, 90, 190},
            {"Reparo na caixa de direção hidráulica", 380, 620, 520, 1240}
    };
}
