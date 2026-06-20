# Arquitetura — ms-desafio

## C4 — Contexto e Containers

```mermaid
C4Context
    title ms-desafio - Diagrama de Contexto e Containers

    Person(parceiro, "Parceiro externo", "Consome a API publicamente")

    System_Boundary(sistema, "ms-desafio") {
        Container(api, "API REST (Spring Boot)", "Java 21 / Spring Boot 4", "Expoe CRUD de Cliente, padrao MVC")
        ContainerDb(db, "H2 Database", "Embedded", "Persiste dados de Cliente")
    }

    Rel(parceiro, api, "Requisicoes HTTP/JSON", "HTTPS")
    Rel(api, db, "Le e escreve", "JDBC")
```

## Camadas internas (MVC)

```mermaid
flowchart LR
    Cliente_HTTP["Parceiro (HTTP/JSON)"] --> Controller
    Controller["ClienteController\n(roteamento HTTP, validacao @Valid)"] --> Service
    Service["ClienteService\n(regra de negocio, transacao)"] --> Repository
    Repository["ClienteRepository\n(Spring Data JPA)"] --> DB[("H2 Database\n(embedded)")]
    Controller -. usa .-> Mapper["ClienteMapper"]
    Service -. usa .-> Mapper
    GlobalExceptionHandler["GlobalExceptionHandler\n(@RestControllerAdvice)"] -. intercepta erros .-> Controller
```

## Explicação das camadas

- **Controller**: recebe requisição HTTP, valida entrada (`@Valid`), delega ao Service, define status code de resposta. Não contém regra de negócio.
- **Service**: contém a regra de negócio (verificação de email duplicado, existência de registro) e controla a transação. Único ponto que conhece tanto DTO quanto Entity.
- **Repository**: interface Spring Data JPA, acesso a dado, sem lógica.
- **Model (Entity)**: representa o schema persistido na tabela `cliente`.
- **DTO**: representa o contrato exposto na API (`ClienteRequestDTO` para entrada, `ClienteResponseDTO` para saída), independente do schema interno.
- **Mapper**: converte Entity↔DTO, evitando que Controller/Service repitam lógica de tradução.
- **GlobalExceptionHandler**: centraliza tratamento de erro, traduz exceções de domínio em respostas HTTP padronizadas (`ProblemDetail`, RFC 9457).

> Este diagrama é escrito em Mermaid. Para abrir/editar no draw.io: **Extras → Edit Diagram**, cole o bloco Mermaid correspondente, e o draw.io renderiza o diagrama nativamente.
