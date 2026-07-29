# PROJETO OFICINA

Sistema de gerenciamento de oficina mecânica: cadastro de clientes, veículos e ordens de serviço, com login via Google (OAuth2) ou email/senha, autorização por papéis (Role) usando JWT, e um modo demo público isolado pra portfólio.

Projeto de estudo (curso Spring Expert / Udemy). Código bom, organizado, mas propositalmente **não enterprise**: sem camada de use-cases separada, sem testes de integração completos, sem CI/CD. Ao mexer no projeto, mantenha esse nível — não introduza abstrações desnecessárias (padrões enterprise, DDD, hexagonal, etc).

## Stack

- Java 21, Spring Boot 3.5.7
- Spring Web, Spring Data JPA, Bean Validation
- Spring Security + OAuth2 Client (login Google) + JWT (io.jsonwebtoken / jjwt 0.12.5)
- PostgreSQL (hospedado no Neon)
- MapStruct 1.5.5 (mapeamento entity <-> DTO)
- Lombok
- Frontend: React (Vite, JavaScript puro) em `frontend/`, rodando em `http://localhost:3000`. Identidade visual "davidev": sidebar fixa grafite (`#12161b`/`#171c22`/`#1d232b`) + conteúdo claro (`#eceef1`) + âmbar de destaque (`#f6b40a`/`#c98d00`); Space Grotesk (títulos), Inter (corpo), JetBrains Mono (placas/IDs/dados técnicos) via Google Fonts no `index.html`.

## Como rodar

Backend (usa JDK 21 — em ambientes sem `JAVA_HOME` configurado, use o JDK em `C:\Users\David\.jdks\ms-21.0.11`):

```
./mvnw.cmd spring-boot:run
```

Variáveis de ambiente obrigatórias (não versionadas, guardadas no run config do IntelliJ em `.idea/workspace.xml`, que não vai pro Git): `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `JWT_SECRET`. Opcionais: `FRONTEND_ORIGIN` (default `http://localhost:3000`, usado no CORS e no redirect pós-login do Google), `BOOTSTRAP_ADMIN_EMAIL` (email que vira `ADMIN` automaticamente no primeiro login/cadastro — substitui promoção manual via `UPDATE` direto no banco), `PORT` (Cloud Run injeta isso; default `8080`).

Frontend:

```
cd frontend
npm install
npm run dev
```

## Estrutura do backend (`src/main/java/com/novanest/projetooficina`)

- `entity` — entidades JPA: `Cliente`, `Veiculo`, `OrdemServico`, `Usuario`
- `dto/<recurso>` — `*RequestDTO` / `*ResponseDTO` por recurso
- `mapper` — interfaces MapStruct (`componentModel = "spring"`)
- `repository` — `JpaRepository`
- `validate` — validação de regra de negócio chamada pelo service (além do Bean Validation nas entidades)
- `service` — regra de negócio, orquestra repository + validate + mapper
- `controller` — REST, com `@PreAuthorize` por Role
- `exception` — exceptions de "não encontrado" + `GlobalExceptionHandler` (`@RestControllerAdvice`) traduz para JSON de erro (404/400/403/500)
- `security` — JWT + OAuth2 + login local (ver seção abaixo)
- `demo` — modo demo público, isolado dos dados reais (ver seção própria)
- `enums` — `Role`, `TipoCliente`, `StatusOS`

Padrão dos controllers: `@RestController` + `@RequiredArgsConstructor`, endpoints REST simples (`GET/POST/PUT/DELETE`), sem versionamento de API.

## Autenticação e autorização

Dois jeitos de logar, ambos terminam no mesmo lugar (um JWT com claims `email`, `role`, `demo`):

- **Google**: usuário loga com Google → Spring Security OAuth2 → `CustomOAuth2UserService` busca ou cria o `Usuario` no banco (role padrão `CLIENTE`, exceto o email configurado em `app.bootstrap-admin-email`, que já nasce `ADMIN`) → `OAuth2LoginSuccessHandler` gera o JWT (`JwtService`) e redireciona para `${app.frontend.origin}/login/callback?token=...`. Falha no login (`failureHandler`) redireciona pro frontend com `?erro=1` em vez de bater num `/login?error` que não existe no backend.
- **Email/senha**: `POST /auth/registrar` e `POST /auth/login` (`AuthController`), senha com hash BCrypt (`UsuarioService.registrar/autenticar`), devolve `{token, usuario}` direto no corpo (mesmo formato de token do fluxo Google).

Depois de logado, o frontend manda o token em `Authorization: Bearer <token>` → `JwtAuthenticationFilter` valida e popula o `SecurityContext` a cada request. Esse mesmo filtro também bloqueia (403) qualquer usuário com `demo=true` que tente acessar algo fora de `/demo/**`, `/auth/**`, `/usuarios/me`, `/health`, `/oauth2/**`, `/login/**` — é o que isola o modo demo do sistema real, mesmo o `admin_demo` tendo role `ADMIN` de verdade.

`SecurityConfig` é stateless (sem sessão), libera `/oauth2/**`, `/login/**`, `/auth/**`, `/demo/**` e `/health`, exige autenticação no resto (401 puro via `HttpStatusEntryPoint`, não redirect — importante pra SPA), usa `@EnableMethodSecurity` para os `@PreAuthorize`, e libera CORS só pra `app.frontend.origin`. `PasswordEncoderConfig` é uma classe separada de propósito: se o bean `PasswordEncoder` vivesse dentro do `SecurityConfig`, criaria dependência circular (`SecurityConfig` → `CustomOAuth2UserService` → `UsuarioService` → `PasswordEncoder`).

Roles (`enums/Role`): `ADMIN` (acesso total, inclusive gerenciar role de outros usuários), `SUPERVISOR` (gerencia operação), `MECANICO` (executa/atualiza status de OS), `ATENDENTE` (cria OS, cadastra cliente/veículo), `CLIENTE` (só o que é dele: `GET /veiculos/meus` e `GET /ordem-servico/minhas`, resolvidos pelo email do token contra `Usuario.cliente` — linkado automaticamente no login se existir um `Cliente` com o mesmo email). **Essa matriz de permissões é considerada estável — não alterar sem pedido explícito.**

Endpoint para o usuário logado: `GET /usuarios/me`. Gestão de roles: `GET /usuarios` e `PATCH /usuarios/{id}/role`, só `ADMIN`.

## Modo demo (`demo/`)

Portfólio público sem arriscar dado real: tudo isolado por uma coluna `demo` (boolean, default `false`) em `Usuario`, `Cliente`, `Veiculo`, `OrdemServico`.

- `DemoController` (`/demo/**`) espelha um subconjunto do CRUD real, mas só enxerga/cria linhas com `demo=true` — `DemoService` reforça isso em toda query. Leitura (`GET`) é pública (`permitAll`); escrita exige `hasRole('ADMIN')`.
- `DemoDataSeeder` cria dois usuários fixos no boot (`admin_demo@oficina.demo` / `admin123`, role `ADMIN`; `visitante@oficina.demo` / `visitante123`, role `CLIENTE`) e reseta os dados demo (4 clientes, 6 veículos, 5 OS fictícios) na subida da aplicação e a cada 6h (`@Scheduled`, `initialDelay` pra não duplicar com o reset do boot). CPF/CNPJ são gerados com dígito verificador válido de verdade (`GeradorDocumento`) porque o Hibernate valida `@CPF`/`@CNPJ` automaticamente ao salvar, mesmo fora do fluxo normal da API.
- `DemoRateLimitFilter`: limite simples por IP (30 escritas/10min) só em `POST/PUT/PATCH/DELETE /demo/**`, em memória (não sobrevive a restart, é o suficiente pro caso real: alguém clicando sem parar num app free-tier).
- O isolamento de acesso do lado do usuário demo (não alcançar rotas reais) é reforçado no `JwtAuthenticationFilter`, não no `DemoController` — ver seção de autenticação acima.

## Paginação

Além dos endpoints de listagem original (sem paginação, mantidos por compatibilidade), existe `GET /clientes/paginado`, `/veiculos/paginado`, `/ordem-servico/paginado` (`?page=&size=&sort=`), devolvendo `Page<T>` do Spring Data. Endpoints novos, não substituem os antigos.

## Testes

`PermissaoMatrizTest` (`@SpringBootTest` + `MockMvc`, banco H2 em memória via `src/test/resources/application.yaml`) cobre a matriz de permissões ponta a ponta: gera um JWT real por role com `JwtService` e bate nos endpoints de verdade — não é teste unitário isolado, exercita o filtro de segurança completo. Cobre também o isolamento do modo demo (usuário demo com role `ADMIN` não alcança rota real).

## Simplificações conhecidas (nível "bom, não profissional")

- `ddl-auto: update` no Hibernate — sem migrations (Flyway/Liquibase). Colunas `NOT NULL` novas em tabela com dados exigem `columnDefinition` com `default` (já aconteceu: `demo boolean not null` sem default quebrou em produção).
- Sem refresh token — o JWT expira em 1h e o usuário precisa logar de novo.
- Menu "Funcionários" do pedido original foi consolidado com "Usuários" — não existe uma entidade `Funcionario` separada de `Usuario` no modelo atual, criar uma seria duplicar dado.
- Rate limit do modo demo é em memória (por instância), não distribuído — não escala pra múltiplas instâncias do backend, mas o Render roda uma só.

## Deploy

Frontend e backend são deploys separados — Vercel não roda o backend Spring Boot (é serverless, não sustenta servidor Java com conexão de banco persistente).

- **Frontend → Vercel**: importar o repo, "Root Directory" = `frontend`, framework Vite (auto-detectado). Env var `VITE_API_URL` = URL pública do backend.
- **Backend → Render/Railway/Fly.io** (qualquer um que aceite Docker): existe um `Dockerfile` na raiz pronto pra isso (build multi-stage com Maven + JRE 21). Env vars: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `JWT_SECRET`, `FRONTEND_ORIGIN` (URL da Vercel, sem barra no final).
- Depois de saber a URL do backend em produção, atualizar no **Google Cloud Console** (OAuth Client) o "Authorized redirect URI" para `https://<url-do-backend>/login/oauth2/code/google`.
- Neon (banco) já é cloud, não muda nada no deploy.

## Workflow de git

David pediu explicitamente (2026-07-28): sempre que um arquivo/classe for alterado, commitar e dar `git push` direto para `origin main`, sem pedir confirmação antes. Não deixar mudanças pendentes só no working tree esperando revisão manual — commitar e subir para o GitHub assim que uma alteração for concluída.

## Convenções de código

- Comentários em blocos `// ===== TITULO =====` acima de métodos de service, em português.
- Nomes de métodos e variáveis em português (`criarCliente`, `buscarPorId`, `atualizarVeiculo`).
- Exceptions de domínio simples, uma por recurso, extendendo `RuntimeException`, tratadas no `GlobalExceptionHandler`.
- DTOs de request usam IDs (`UUID clienteId`) para referenciar outras entidades, nunca o objeto completo.
