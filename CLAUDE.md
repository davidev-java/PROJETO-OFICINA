# PROJETO OFICINA

Sistema de gerenciamento de oficina mecânica: cadastro de clientes, veículos e ordens de serviço, com login via Google (OAuth2) e autorização por papéis (Role) usando JWT.

Projeto de estudo (curso Spring Expert / Udemy). Código bom, organizado, mas propositalmente **não enterprise**: sem camada de use-cases separada, sem testes de integração completos, sem CI/CD, sem Docker. Ao mexer no projeto, mantenha esse nível — não introduza abstrações desnecessárias (padrões enterprise, DDD, hexagonal, etc).

## Stack

- Java 21, Spring Boot 3.5.7
- Spring Web, Spring Data JPA, Bean Validation
- Spring Security + OAuth2 Client (login Google) + JWT (io.jsonwebtoken / jjwt 0.12.5)
- PostgreSQL (hospedado no Neon)
- MapStruct 1.5.5 (mapeamento entity <-> DTO)
- Lombok
- Frontend: React (Vite, JavaScript puro) em `frontend/`, rodando em `http://localhost:3000`

## Como rodar

Backend (usa JDK 21 — em ambientes sem `JAVA_HOME` configurado, use o JDK em `C:\Users\David\.jdks\ms-21.0.11`):

```
./mvnw.cmd spring-boot:run
```

Variáveis de ambiente obrigatórias (não versionadas): `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `JWT_SECRET`.

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
- `security` — JWT + OAuth2 (ver seção abaixo)
- `enums` — `Role`, `TipoCliente`, `StatusOS`

Padrão dos controllers: `@RestController` + `@RequiredArgsConstructor`, endpoints REST simples (`GET/POST/PUT/DELETE`), sem versionamento de API.

## Autenticação e autorização

Fluxo: usuário loga com Google → Spring Security OAuth2 → `CustomOAuth2UserService` busca ou cria o `Usuario` no banco (role padrão `CLIENTE`) → `OAuth2LoginSuccessHandler` gera um JWT (`JwtService`) e redireciona para `app.frontend.redirect-url` (`http://localhost:3000/login/callback?token=...`) → o frontend guarda o token e manda em `Authorization: Bearer <token>` nas próximas chamadas → `JwtAuthenticationFilter` valida o token e popula o `SecurityContext` a cada request.

`SecurityConfig` é stateless (sem sessão), libera `/oauth2/**` e `/login/**`, exige autenticação no resto, e usa `@EnableMethodSecurity` para os `@PreAuthorize` nos controllers.

Roles (`enums/Role`): `ADMIN` (acesso total), `SUPERVISOR` (gerencia operação), `MECANICO` (executa/atualiza status de OS), `ATENDENTE` (cria OS, cadastra cliente/veículo), `CLIENTE` (só visualiza o que é dele — endpoints de CLIENTE ainda não têm filtro por dono, é uma simplificação conhecida).

Endpoint para o usuário logado: `GET /usuarios/me`. Gestão de roles: `GET /usuarios` e `PATCH /usuarios/{id}/role`, só `ADMIN`.

## Simplificações conhecidas (nível "bom, não profissional")

- `CLIENTE` não tem endpoints filtrados pelo próprio cadastro (ex: "minhas OS") — só as roles de staff usam a API de OS/cliente/veículo hoje.
- Sem paginação nas listagens (`findAll` puro).
- Sem testes automatizados além do smoke test de contexto do Spring Boot.
- `ddl-auto: update` no Hibernate — sem migrations (Flyway/Liquibase).
- Sem refresh token — o JWT expira em 1h e o usuário precisa logar de novo.

## Workflow de git

David pediu explicitamente (2026-07-28): sempre que um arquivo/classe for alterado, commitar e dar `git push` direto para `origin main`, sem pedir confirmação antes. Não deixar mudanças pendentes só no working tree esperando revisão manual — commitar e subir para o GitHub assim que uma alteração for concluída.

## Convenções de código

- Comentários em blocos `// ===== TITULO =====` acima de métodos de service, em português.
- Nomes de métodos e variáveis em português (`criarCliente`, `buscarPorId`, `atualizarVeiculo`).
- Exceptions de domínio simples, uma por recurso, extendendo `RuntimeException`, tratadas no `GlobalExceptionHandler`.
- DTOs de request usam IDs (`UUID clienteId`) para referenciar outras entidades, nunca o objeto completo.
