# Biblioteca Online

Projeto do Tech Challenge da Fase 2 (FIAP). É uma API REST pra gerenciar uma biblioteca:
cadastro de livros e usuários, empréstimos, devoluções, reservas e alguns relatórios de uso.

Feito em Java 21 com Spring Boot 3, banco PostgreSQL rodando em Docker (e H2 em memória pra
desenvolvimento/testes), documentação no Swagger.

## Tecnologias

- Java 21
- Spring Boot 3.3 (Web, Data JPA, Validation, Actuator)
- PostgreSQL 16 (Docker) / H2 (dev e testes)
- springdoc-openapi (Swagger)
- Maven (tem o wrapper `mvnw`, então nem precisa do Maven instalado)
- Docker + Docker Compose
- JUnit 5 / AssertJ / Testcontainers

## Como rodar

### Com Docker (mais simples)

Só precisa do Docker instalado:

```bash
docker compose up --build
```

Isso sobe a API e o Postgres. Já vem com o profile `seed` ligado, então cria uns dados de
exemplo pra você não começar com o banco vazio.

- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

Pra escalar a aplicação (ela é stateless):

```bash
docker compose up --build --scale app=3
```

### Local (sem Docker)

Precisa do JDK 21. O resto o wrapper baixa.

```bash
# Linux/macOS
./mvnw spring-boot:run -Dspring-boot.run.profiles=seed

# Windows
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=seed"
```

Sobe na porta 8080 usando o H2 em memória. O console do H2 fica em
http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:biblioteca`, user `sa`, sem senha).

### Testes

```bash
./mvnw test
```

Obs: tem um teste com Testcontainers que sobe um Postgres de verdade. Se a máquina não tiver
Docker, ele se desabilita sozinho e o resto roda normal.

## Sobre data e hora

Tudo roda em UTC (GMT+0). O fuso é fixado na subida da aplicação, no Hibernate e nos
containers. As datas saem no formato ISO-8601 (ex.: `2026-06-15T13:45:00Z` pra instante e
`2026-06-29` pra data). O prazo de devolução é a data do empréstimo + 14 dias (dá pra mudar
pela variável `LOAN_DAYS`).

## Endpoints

Tudo embaixo de `/api`. A lista completa com exemplos está no Swagger, mas em resumo:

### Livros
- `POST /livros` – cadastra
- `POST /livros/lote` – cadastra vários de uma vez
- `GET /livros?titulo=&autor=&isbn=&somenteDisponiveis=&page=&size=` – busca com filtros e paginação
- `GET /livros/{id}`
- `PUT /livros/{id}`
- `DELETE /livros/{id}` – não deixa apagar se tiver empréstimo ativo

### Usuários
- `POST /usuarios`
- `GET /usuarios`
- `GET /usuarios/{id}`
- `PUT /usuarios/{id}` – altera os dados; `ativo` é opcional (envie `false` pra inativar)
- `DELETE /usuarios/{id}` – mesma trava do livro
- `GET /usuarios/{id}/emprestimos` – histórico

Body completo do `PUT /usuarios/{id}` (o `ativo` é opcional – se omitir, mantém o valor atual):

```json
{
  "nome": "Maria Silva",
  "email": "maria.silva@fiap.com.br",
  "tipo": "ALUNO",
  "ativo": false
}
```

Não dá pra excluir um usuário (nem um livro) que já tem histórico de empréstimos ou reservas, pra não
perder o histórico. Quando isso acontece a API responde `409` explicando; se a ideia é só tirar o usuário
de circulação, o caminho é inativá-lo nesse mesmo `PUT` com `"ativo": false`.

### Empréstimos
- `POST /emprestimos` – registra e calcula a previsão de devolução
- `PATCH /emprestimos/{id}/devolucao` – devolve (e libera a próxima reserva da fila)
- `GET /emprestimos?status=`
- `GET /emprestimos/{id}`

### Reservas
- `POST /reservas` – só dá pra reservar livro que está indisponível
- `DELETE /reservas/{id}` – cancela
- `GET /reservas?usuarioId=`

### Relatórios
- `GET /relatorios/livros-mais-emprestados` – top 20
- `GET /relatorios/emprestimos-ativos` – o que está emprestado agora + previsão de devolução

Exemplo rápido:

```bash
curl -X POST http://localhost:8080/api/livros -H "Content-Type: application/json" -d '{
  "titulo": "Clean Code", "autor": "Robert C. Martin",
  "isbn": "9780132350884", "quantidadeTotal": 3
}'
```

## Regras principais

- Livro controla disponibilidade por número de exemplares (total x disponível). ISBN é único.
- Usuário pode ser ALUNO, PROFESSOR ou MEMBRO. E-mail único. Usuário inativo não empresta.
- Empréstimo desconta um exemplar; devolução devolve. O atraso é calculado na hora da consulta
  (data prevista vs. hoje).
- Reserva entra numa fila por livro (FIFO). Quando alguém devolve, a primeira reserva da fila
  é atendida automaticamente.

## Sobre o banco e performance

- Paginação em todas as listagens.
- Relatórios agregados direto no banco (GROUP BY / COUNT) e `JOIN FETCH` pra evitar N+1.
- Índices nos campos mais usados (isbn, email, status, FKs).
- Cadastro em lote e lock otimista (`@Version`) no livro pra concorrência.

## Docker

- Dockerfile multi-stage (compila com Maven e roda só com a JRE, em usuário não-root).
- O compose sobe app + Postgres, com healthcheck no banco — a API só inicia depois que o
  banco está pronto.
- App stateless, dá pra escalar horizontal.

## Documentação

- Swagger em `/swagger-ui.html`.
- Javadoc: `./mvnw javadoc:javadoc` (sai em `target/site/apidocs`).
