# ms-desafio

API REST de Clientes — Desafio Final do Bootcamp Arquiteto de Software.
Arquitetura MVC (Controller → Service → Repository → Entity), com DTOs
separando contrato externo de schema de persistência, e H2 embedded.

## Stack

Java 21, Spring Boot 4, Spring Data JPA, H2 Database, Jakarta Validation,
springdoc-openapi, Lombok.

## Como executar

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080/api/clientes`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:msdesafio`, user `sa`, sem senha)

## Como testar

```bash
mvn test
```

## Endpoints

| Método | Path | Ação |
|--------|------|------|
| POST   | `/api/clientes` | Criar cliente |
| GET    | `/api/clientes` | Listar todos |
| GET    | `/api/clientes/{id}` | Buscar por id |
| GET    | `/api/clientes/nome/{nome}` | Buscar por nome (contém, case-insensitive) |
| GET    | `/api/clientes/contar` | Contar total de registros |
| PUT    | `/api/clientes/{id}` | Atualizar cliente |
| DELETE | `/api/clientes/{id}` | Remover cliente |

## Estrutura de pastas

```
src/main/java/br/com/pelorca/desafio/msdesafio/
├── controller/   # Controladores REST - roteamento HTTP, validação, status code
├── service/      # Regra de negócio e transação
├── repository/   # Interfaces Spring Data JPA - acesso a dado
├── model/        # Entidades JPA - schema de persistência
├── dto/          # Contratos de entrada/saída da API
├── mapper/       # Conversão Entity <-> DTO
├── exception/    # Exceções de domínio e handler global de erro
└── config/       # Configurações (OpenAPI, Jackson snake_case)
```

Diagrama arquitetural completo em [`docs/architecture.md`](docs/architecture.md).
