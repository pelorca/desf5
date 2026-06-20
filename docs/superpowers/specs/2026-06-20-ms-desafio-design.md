# Design — ms-desafio (Bootcamp Arquiteto de Software / Desafio Final)

## Contexto

Desafio do bootcamp pede: API REST padrão arquitetural MVC, expondo CRUD (e
um pouco mais) sobre um domínio de Cliente/Produto/Pedido, voltada a
parceiros externos de uma empresa de vendas online. Entregáveis: diagrama
arquitetural, estrutura de pastas documentada, e opcionalmente código
funcionando com persistência.

Domínio escolhido: **Cliente**.

## Stack

- Java 21
- Spring Boot 4.x
- Maven — groupId `br.com.pelorca.desafio`, artifactId `ms-desafio`
- Spring Web, Spring Data JPA, H2 Database (embedded, sobe junto com a
  aplicação, sem dependência externa)
- Jakarta Validation (bean validation nos DTOs de request)
- springdoc-openapi-starter-webmvc-ui (Swagger UI + OpenAPI JSON)
- Lombok (reduz boilerplate de getters/setters/builders)
- JUnit 5 + Mockito (testes unitários de Service, `@WebMvcTest`/
  `@SpringBootTest` + MockMvc para Controller)

## Arquitetura

Padrão MVC em camadas clássico: `Controller → Service → Repository →
Entity`, com DTOs separando o contrato externo (API) do schema de
persistência interno (Entity JPA). Mapeamento Entity↔DTO feito manualmente
(sem MapStruct), via métodos estáticos em uma classe `ClienteMapper`.

Alternativas consideradas e descartadas:
- **Hexagonal/Clean Architecture** — overkill para CRUD de entidade única;
  o enunciado pede explicitamente MVC, não ports & adapters.
- **CQRS** (separar comandos de queries) — sem ganho real no escopo deste
  desafio.

### Estrutura de pastas

```
src/main/java/br/com/pelorca/desafio/msdesafio/
├── MsDesafioApplication.java
├── controller/
│   └── ClienteController.java       # endpoints HTTP, valida @RequestBody, delega ao Service
├── service/
│   └── ClienteService.java          # regra de negócio, orquestra Repository, lança exceptions de domínio
├── repository/
│   └── ClienteRepository.java       # extends JpaRepository<Cliente, Long>, query method de busca por nome
├── model/
│   └── Cliente.java                 # @Entity JPA
├── dto/
│   ├── ClienteRequestDTO.java       # input (POST/PUT), com bean validation
│   └── ClienteResponseDTO.java      # output
├── mapper/
│   └── ClienteMapper.java           # métodos estáticos de conversão Entity↔DTO
├── exception/
│   ├── ClienteNotFoundException.java
│   └── GlobalExceptionHandler.java  # @RestControllerAdvice, retorna ProblemDetail (RFC 9457)
└── config/
    └── OpenApiConfig.java           # metadados Swagger (título, versão, descrição)

src/main/resources/
├── application.yml                  # config H2, JPA, springdoc
└── data.sql (opcional)              # seed de dados de exemplo

src/test/java/.../
├── service/ClienteServiceTest.java
└── controller/ClienteControllerTest.java
```

Papel de cada componente:
- **Model**: forma do dado persistido (schema da tabela via JPA).
- **DTO**: forma do dado exposto na API — separa contrato externo do
  schema interno, permite evoluir um sem quebrar o outro.
- **Repository**: acesso a dado; Spring Data JPA gera o SQL a partir da
  assinatura do método.
- **Service**: regra de negócio e transação; ponto único de orquestração
  entre Controller e Repository.
- **Controller**: roteamento HTTP, validação de entrada, status code;
  sem lógica de negócio.
- **Mapper**: tradução Entity↔DTO, isolada para não poluir Service.
- **GlobalExceptionHandler**: centraliza tratamento de erro, resposta
  padronizada via `ProblemDetail`.

## Modelo de dados

Entidade `Cliente`:

| campo     | tipo   | regra                                  |
|-----------|--------|-----------------------------------------|
| id        | Long   | `@Id`, `GenerationType.IDENTITY`        |
| nome      | String | `@NotBlank`, máx. 120 caracteres        |
| email     | String | `@NotBlank`, `@Email`, único            |
| telefone  | String | `@Pattern` (formato BR, opcional), máx. 20 |
| endereco  | String | máx. 200 caracteres                     |

## Endpoints

Base path: `/api/clientes`

| Método | Path                  | Ação        | Status sucesso |
|--------|-----------------------|-------------|-----------------|
| POST   | `/api/clientes`       | criar       | 201             |
| GET    | `/api/clientes`       | findAll     | 200             |
| GET    | `/api/clientes/{id}`  | findById    | 200 / 404       |
| GET    | `/api/clientes/nome/{nome}` | findByName (contains, case-insensitive) | 200 |
| GET    | `/api/clientes/contar`| count       | 200             |
| PUT    | `/api/clientes/{id}`  | update      | 200 / 404       |
| DELETE | `/api/clientes/{id}`  | delete      | 204 / 404       |

## Tratamento de erro

Respostas de erro no formato `ProblemDetail` (RFC 9457, suporte nativo do
Spring Boot, sem dependência extra):
- Corpo de request inválido (bean validation falha) → 400, detalha campo
  e mensagem.
- `id` inexistente em get/update/delete → 404.
- Email duplicado na criação/atualização → 409.

## Documentação da API

springdoc-openapi expõe automaticamente:
- Swagger UI em `/swagger-ui.html`
- OpenAPI JSON em `/v3/api-docs`

Metadados (título, versão, descrição) configurados em `OpenApiConfig`.

## Diagrama arquitetural

Formato: **Mermaid** (texto, versionável no repositório, renderiza em
GitHub/GitLab/VSCode). draw.io importa Mermaid nativamente (Extras → Edit
Diagram → cola código Mermaid), então o mesmo artefato atende ao pedido do
enunciado ("diagrama de preferência, ferramentas como draw.io") sem
duplicar esforço.

Diagrama (C4 Context + Container) salvo em `docs/architecture.md`, mostrando:
- Parceiro externo (ator) → `ms-desafio` (Spring Boot container)
- `ms-desafio` → H2 Database (embedded)
- Camadas internas: Controller → Service → Repository

## Testes

- `ClienteServiceTest`: unit, Mockito mockando `ClienteRepository`,
  cobre cada método de negócio (listarTodos, buscarPorId, buscarPorNome,
  salvar, deletar, contar) incluindo caso "não encontrado".
- `ClienteControllerTest`: `@SpringBootTest` + `MockMvc`, cobre cada
  endpoint incluindo validação de erro (400/404/409).

## Entregáveis finais

1. `docs/architecture.md` — diagrama C4 em Mermaid + explicação das
   camadas MVC.
2. Estrutura de pastas (definida acima) com README explicando o papel de
   cada pasta.
3. Código funcional completo: CRUD + contar + findAll + findById +
   findByName.
4. Persistência H2 funcionando (diferencial citado no enunciado).
5. Swagger UI (`/swagger-ui.html`) e suíte de testes unit/integration.
