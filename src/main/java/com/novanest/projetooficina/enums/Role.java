package com.novanest.projetooficina.enums;

public enum Role {
    ADMIN,       // dono da oficina, acesso total
    SUPERVISOR,  // gerencia operação, mas não configurações do sistema
    MECANICO,    // executa e atualiza status das ordens de serviço
    ATENDENTE,   // faz atendimento, cria OS, cadastra cliente/veículo
    CLIENTE      // só visualiza o que é dele
}
