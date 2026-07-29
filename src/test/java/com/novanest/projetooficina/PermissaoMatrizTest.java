package com.novanest.projetooficina;

import com.novanest.projetooficina.entity.Usuario;
import com.novanest.projetooficina.enums.Role;
import com.novanest.projetooficina.repository.UsuarioRepository;
import com.novanest.projetooficina.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Cobre a matriz de permissoes descrita no CLAUDE.md: garante que uma
// mudanca futura nao afrouxe/aperte acesso sem que um teste quebre.
@SpringBootTest
@AutoConfigureMockMvc
class PermissaoMatrizTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Persiste (uma vez) um usuario real de teste pra cada role, ja que
    // endpoints como /veiculos/meus resolvem o usuario logado pelo email
    // do token contra o banco, não confiam só no conteúdo do JWT.
    private String tokenPara(Role role) {
        String email = "teste-" + role.name().toLowerCase() + "@exemplo.com";

        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario novo = new Usuario();
            novo.setEmail(email);
            novo.setNome("Usuário Teste");
            novo.setRole(role);
            novo.setDemo(false);
            return usuarioRepository.save(novo);
        });

        return "Bearer " + jwtService.gerarToken(usuario);
    }

    // ===== CLIENTES: leitura/escrita so pra staff, CLIENTE nao acessa =====

    @Test
    void listarClientes_semToken_retorna401() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarClientes_comRoleCliente_retorna403() throws Exception {
        mockMvc.perform(get("/clientes").header("Authorization", tokenPara(Role.CLIENTE)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarClientes_comRoleAtendente_retorna200() throws Exception {
        mockMvc.perform(get("/clientes").header("Authorization", tokenPara(Role.ATENDENTE)))
                .andExpect(status().isOk());
    }

    @Test
    void criarCliente_comRoleCliente_retorna403() throws Exception {
        mockMvc.perform(post("/clientes")
                        .header("Authorization", tokenPara(Role.CLIENTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletarCliente_comRoleAtendente_retorna403() throws Exception {
        // Matriz atual: deletar cliente é só ADMIN/SUPERVISOR, ATENDENTE não pode
        mockMvc.perform(delete("/clientes/" + java.util.UUID.randomUUID())
                        .header("Authorization", tokenPara(Role.ATENDENTE)))
                .andExpect(status().isForbidden());
    }

    // ===== ORDEM DE SERVICO: criar so ADMIN/SUPERVISOR/ATENDENTE =====

    @Test
    void criarOrdemServico_comRoleMecanico_retorna403() throws Exception {
        mockMvc.perform(post("/ordem-servico")
                        .header("Authorization", tokenPara(Role.MECANICO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    // ===== NOVOS ENDPOINTS "MEUS DADOS" (so role CLIENTE) =====

    @Test
    void meusVeiculos_comRoleCliente_retorna200() throws Exception {
        mockMvc.perform(get("/veiculos/meus").header("Authorization", tokenPara(Role.CLIENTE)))
                .andExpect(status().isOk());
    }

    @Test
    void meusVeiculos_comRoleAtendente_retorna403() throws Exception {
        mockMvc.perform(get("/veiculos/meus").header("Authorization", tokenPara(Role.ATENDENTE)))
                .andExpect(status().isForbidden());
    }

    // ===== MODO DEMO: leitura publica, escrita so ADMIN, isolado dos dados reais =====

    @Test
    void listarClientesDemo_semToken_retorna200() throws Exception {
        mockMvc.perform(get("/demo/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    void criarClienteDemo_comRoleCliente_retorna403() throws Exception {
        mockMvc.perform(post("/demo/clientes")
                        .header("Authorization", tokenPara(Role.CLIENTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    // ===== USUARIO DEMO NAO ALCANCA ENDPOINT REAL, mesmo com role ADMIN =====

    @Test
    void usuarioDemoAdmin_tentandoRotaReal_retorna403() throws Exception {
        Usuario admin = new Usuario();
        admin.setId(99L);
        admin.setEmail("admin_demo@oficina.demo");
        admin.setNome("Admin Demo");
        admin.setRole(Role.ADMIN);
        admin.setDemo(true);

        String token = "Bearer " + jwtService.gerarToken(admin);

        mockMvc.perform(get("/clientes").header("Authorization", token))
                .andExpect(status().isForbidden());
    }
}
