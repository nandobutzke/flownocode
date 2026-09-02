# flow-no-code

Monorepo de um **Workflow Engine** no estilo n8n: a API Java descreve e executa flows como JSON; o frontend Next.js monta o grafo visualmente e só então chama `POST /flows` e `POST /flows/{id}/execute`.

| Pasta | Stack |
|---|---|
| [`backend/`](backend/) | Java 17 + Spring Boot 3 — engine, persistência, Bedrock |
| [`frontend/`](frontend/) | React 19 + Next.js — editor dark, XYFlow, draft no `localStorage` |

O browser fala só com o Next (`:3000`). Rewrites encaminham `/api/flows` para o Spring (`:8080`). A API não muda de contrato.

---

## Como rodar

### Frontend (editor)

```bash
cd frontend
cp .env.example .env   # API_URL=http://localhost:8080
npm install
npm run dev
```

Abre `http://localhost:3000`. Monte os blocos, **Save** (um `POST /flows`) e **Execute** (variáveis chave/valor). Depois do save o canvas fica somente leitura; **New** gera outros UUIDs.

### Backend + Postgres (Docker)

O `docker-compose.yml` na raiz sobe a API e o banco. O Next continua fora do compose.

| Serviço | Container | Descrição |
|---|---|---|
| `api` | `flownocode-api` | API Java 17 + Spring Boot 3 (`backend/docker/api/Dockerfile`, porta `8080`) |
| `postgres` | `flownocode-postgres` | PostgreSQL 16 (porta `5432`, volume persistente) |

```bash
docker compose up --build
```

| URL | O que é |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Documentação interativa (Swagger) |
| `http://localhost:8080/api-docs` | Spec OpenAPI em JSON |

```bash
docker compose down
```

> API **sem Docker** (H2 em memória):
> ```bash
> cd backend && mvn spring-boot:run
> ```
> Console H2: `http://localhost:8080/h2-console`.

---

## Endpoints

### `POST /flows` — Criar um flow

Recebe a definição completa do workflow e persiste no banco.

**Request body:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Prime Number Validation Flow",
  "startBlockId": "11111111-1111-1111-1111-111111111111",
  "blocks": [
    {
      "id": "11111111-1111-1111-1111-111111111111",
      "type": "SET_VARIABLE",
      "nextBlockId": "22222222-2222-2222-2222-222222222222",
      "config": {
        "variable": "divisor",
        "value": 2
      }
    },
    {
      "id": "22222222-2222-2222-2222-222222222222",
      "type": "MOD",
      "nextBlockId": "33333333-3333-3333-3333-333333333333",
      "config": {
        "left": "input",
        "right": "divisor",
        "resultVariable": "remainder"
      }
    },
    {
      "id": "33333333-3333-3333-3333-333333333333",
      "type": "CONDITION",
      "config": {
        "left": "remainder",
        "operator": "==",
        "right": 0,
        "trueNextBlockId": "44444444-4444-4444-4444-444444444444",
        "falseNextBlockId": "55555555-5555-5555-5555-555555555555"
      }
    },
    {
      "id": "44444444-4444-4444-4444-444444444444",
      "type": "END",
      "config": {
        "result": false
      }
    },
    {
      "id": "55555555-5555-5555-5555-555555555555",
      "type": "INCREMENT",
      "nextBlockId": "66666666-6666-6666-6666-666666666666",
      "config": {
        "variable": "divisor"
      }
    },
    {
      "id": "66666666-6666-6666-6666-666666666666",
      "type": "CONDITION",
      "config": {
        "left": "divisor",
        "operator": "<",
        "right": "input",
        "trueNextBlockId": "22222222-2222-2222-2222-222222222222",
        "falseNextBlockId": "77777777-7777-7777-7777-777777777777"
      }
    },
    {
      "id": "77777777-7777-7777-7777-777777777777",
      "type": "END",
      "config": {
        "result": true
      }
    }
  ]
}
```

**Resposta de sucesso — `201 Created`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Prime Number Validation Flow",
  "startBlockId": "11111111-1111-1111-1111-111111111111",
  "blockCount": 7,
  "createdAt": "2026-05-27T00:00:00"
}
```

**Validações:**
- `id`, `name` e `startBlockId` são obrigatórios
- A lista de `blocks` não pode ser vazia
- O `startBlockId` deve referenciar um `id` existente na lista de blocks
- Os `id` dos blocks não podem se repetir no mesmo payload
- Nenhum `id` de block enviado pode já existir no banco (validação em batch antes do save)
- O `id` do flow não pode já existir no banco

---

### `POST /flows/{flowId}/execute` — Executar um flow

Busca o flow salvo e executa a engine com as variáveis de entrada fornecidas.

**Request body:**
```json
{
  "input": 4
}
```

**Resposta de sucesso — `200 OK`:**
```json
{
  "flowId": "550e8400-e29b-41d4-a716-446655440000",
  "flowName": "Prime Number Validation Flow",
  "output": { "result": false }
}
```

**Tratamento de erros:**

| Situação | HTTP |
|---|---|
| Campo obrigatório ausente ou inválido | `400 Bad Request` |
| Flow com o mesmo `id` já cadastrado | `409 Conflict` |
| Block com o mesmo `id` já cadastrado | `409 Conflict` |
| IDs de blocks duplicados no payload | `422 Unprocessable Entity` |
| `flowId` não encontrado no banco | `404 Not Found` |
| `startBlockId` não existe nos blocks | `422 Unprocessable Entity` |
| Erro inesperado | `500 Internal Server Error` |

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Flow not found with id: 550e8400-...",
  "timestamp": "2026-05-27T00:00:00"
}
```

## Stack de tecnologias

| Tecnologia | Versão | Informações |
|---|---|---|
| Java | 17 | LTS estável |
| Spring Boot | 3.3 | Framework padrão de mercado para APIs REST em Java |
| Spring Data JPA + Hibernate | 3.3 | ORM para persistir o modelo de domínio sem SQL manual |
| H2 Database | runtime | Banco em memória — zero configuração para rodar e testar localmente |
| PostgreSQL | 16 | Banco de dados configurado via Docker Compose |
| Lombok | latest | Elimina boilerplate de getters, setters e construtores |
| springdoc-openapi | 2.5 | Gera documentação Swagger automaticamente a partir do código |
| JUnit 5 + Mockito | via Spring | Testes |

---


## Estrutura do projeto

```
backend/
├── docker/
│   ├── api/Dockerfile      → Build multi-stage da API (Java 17 + Spring Boot 3)
│   └── postgres/Dockerfile → Imagem do PostgreSQL 16
├── docs/                   → Contexto AWS / diagrama
└── src/main/java/com/flownocode/api/
    ├── controller/         → Endpoints REST (entrada da API)
    ├── service/            → Regras de negócio e orquestração
    ├── domain/             → Entidades JPA (o que vai para o banco)
    ├── dto/                → Request / response
    ├── engine/             → Workflow engine + executors
    ├── exception/          → Tratamento global de erros
    ├── repository/         → Spring Data JPA
    └── config/             → Swagger / Bedrock

frontend/
└── src/                    → Editor Next.js (paleta, canvas, inspector, Save/Execute)

docker-compose.yml          → Serviços `api` e `postgres` (context em backend/)
```

---

## Modelo de domínio

O banco de dados tem duas tabelas:

### `flows`
Representa a definição de um workflow. É o "fluxograma completo".

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | UUID | Identificador único, fornecido pelo cliente no JSON |
| `name` | String | Nome do flow |
| `startBlockId` | UUID | Qual bloco executar primeiro |
| `createdAt` | Timestamp | Criação automática |
| `updatedAt` | Timestamp | Atualização automática |

### `blocks`
Representa cada "caixinha" do fluxograma. Um Flow tem vários Blocks.

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | UUID | Identificador único, fornecido pelo cliente no JSON |
| `type` | Enum | Tipo do bloco: `SET_VARIABLE`, `MOD`, `CONDITION`, etc. |
| `nextBlockId` | UUID | Qual bloco vem depois (null = fim do caminho) |
| `config` | TEXT (JSON) | Parâmetros específicos de cada tipo de bloco |
| `flow_id` | FK | Qual Flow esse bloco pertence |

**Por que o `id` é fornecido pelo cliente e não gerado automaticamente?**
Porque os blocos se referenciam entre si pelo `id` (via `nextBlockId`, `trueNextBlockId`, `falseNextBlockId`). Se o banco gerasse os IDs, o cliente teria que fazer um POST por bloco para descobrir cada ID — impraticável. Ao deixar o cliente definir os UUIDs, o JSON inteiro do flow é enviado em uma única requisição, com todas as referências já resolvidas.

**Por que o `config` é um `Map<String, Object>` serializado como JSON?**
Cada tipo de bloco tem parâmetros diferentes:
- `SET_VARIABLE` precisa de `variable` e `value`
- `MOD` precisa de `left`, `right` e `resultVariable`
- `CONDITION` precisa de `left`, `operator`, `right`, `trueNextBlockId` e `falseNextBlockId`

Criar uma coluna por parâmetro seria inviável e rígido. Um campo JSON livre permite que cada bloco carregue exatamente o que precisa, e novos tipos de bloco não exigem alteração no schema do banco.

---

## A Workflow Engine

A engine fica no pacote `engine/` e é a parte mais importante do projeto.

### Como funciona (o loop de execução)

```
Flow recebido + variáveis de entrada
          ↓
Monta Map<UUID, Block> para busca O(1)
          ↓
Pega o bloco inicial: blockMap.get(flow.getStartBlockId())
          ↓
┌─────────────────────────────────────┐
│  enquanto (bloco atual != null):    │
│    1. pega o executor do bloco      │
│    2. executa                       │
│    3. recebe o próximo blockId      │
│    4. bloco atual = blockMap[id]    │
└─────────────────────────────────────┘
          ↓
Retorna ExecutionContext (contém o resultado final)
```

São 10 linhas de código. A engine não sabe nada sobre regras de negócio — ela só sabe "pegar executor, executar, avançar para o próximo".

### ExecutionContext — a memória compartilhada

É uma classe simples que funciona como a "memória de trabalho" durante a execução. Todos os blocos leem e escrevem variáveis nesse objeto.

```java
ExecutionContext context = new ExecutionContext(Map.of("input", 17));

// Após SET_VARIABLE:
context.get("divisor")   // → 2

// Após MOD:
context.get("remainder") // → 1

// Após END:
context.getResult()      // → true ou false
```

O `input` original é preservado como cópia imutável — nenhum bloco consegue sobrescrever o que foi enviado originalmente.

---

## Design Pattern: Strategy

O principal padrão de projeto utilizado é o **Strategy Pattern** — aplicado aos executores de blocos.

### O problema que ele resolve

Sem o pattern, a engine precisaria de um `if/else` ou `switch` gigante:

```java
// ❌ O que NÃO foi feito
if (block.getType() == SET_VARIABLE) {
    // lógica do set variable
} else if (block.getType() == MOD) {
    // lógica do mod
} else if (block.getType() == CONDITION) {
    // lógica da condition
} // ... e assim por diante para cada novo tipo
```

Isso viola o **Princípio Aberto/Fechado** (Open/Closed Principle do SOLID): cada vez que um novo tipo de bloco fosse criado, seria necessário modificar a engine.

### A solução: interface + uma classe por tipo

```java
// ✅ O que foi feito
public interface BlockExecutor {
    BlockResult execute(Block block, ExecutionContext context);
    BlockType getType(); // "eu sou responsável por qual tipo?"
}
```

Cada tipo de bloco tem sua própria classe:

| Classe | Tipo | O que faz |
|---|---|---|
| `SetVariableBlockExecutor` | `SET_VARIABLE` | Lê `variable` e `value` do config, salva no contexto |
| `ModBlockExecutor` | `MOD` | Calcula `left % right`, salva em `resultVariable` |
| `ConditionBlockExecutor` | `CONDITION` | Avalia `left operator right`, decide qual bloco vem a seguir |
| `IncrementBlockExecutor` | `INCREMENT` | Soma +1 à variável informada |
| `EndBlockExecutor` | `END` | Salva o resultado final no contexto, para a execução |

### BlockExecutorRegistry — o despachante

O registry é quem conecta tudo. Ao iniciar, o Spring injeta automaticamente **todos os beans** que implementam `BlockExecutor`, e o registry constrói um `Map<BlockType, BlockExecutor>`:

```java
// O Spring descobre todos os @Component que implementam BlockExecutor
// e injeta como List<BlockExecutor>
public BlockExecutorRegistry(List<BlockExecutor> executors) {
    this.executors = executors.stream()
            .collect(Collectors.toMap(BlockExecutor::supports, Function.identity()));
}
```

Resultado: para adicionar um novo tipo de bloco basta criar uma nova classe com `@Component` e implementar `BlockExecutor`. O registry e a engine não precisam mudar uma linha.

### BlockResult — o retorno da execução

Cada executor retorna um `BlockResult` que informa à engine qual bloco executar a seguir:

```java
BlockResult.next(uuid)  // → continua com esse bloco
BlockResult.stop()      // → encerra a execução (END block)
```

O `CONDITION` é o único bloco que devolve um `nextBlockId` diferente do campo `block.getNextBlockId()` — ele decide em runtime com base na avaliação da condição.

---

## ConfigValueResolver — valores dinâmicos vs. literais

Um desafio sutil da engine: nos configs dos blocos, um valor pode ser:
- **O nome de uma variável:** `"left": "remainder"` → busca `remainder` no contexto
- **Um valor literal:** `"right": 0` → usa `0` diretamente

```java
// "remainder" → busca no contexto → retorna 1 (número)
// 0           → é um Number → retorna 0 diretamente
ConfigValueResolver.resolveNumber(configValue, context)
```

Esse utilitário é compartilhado entre `ModBlockExecutor`, `ConditionBlockExecutor` e `IncrementBlockExecutor` — extraído para evitar duplicação.

---

## Exemplo completo: Validação de Número Primo

Para demonstrar a engine, foi implementado um flow que verifica se um número é primo usando apenas os blocos disponíveis — sem escrever uma linha de lógica específica para primos.

### O algoritmo em pseudocódigo

```
divisor = 2
enquanto (divisor < input):
    remainder = input % divisor
    se remainder == 0:
        retorna false  (divisível → não é primo)
    divisor = divisor + 1
retorna true  (não encontrou divisor → é primo)
```

### O mesmo algoritmo como Flow

```
[SET_VARIABLE: divisor=2]
        ↓
[MOD: remainder = input % divisor]
        ↓
[CONDITION: remainder == 0?]
   true ↓              ↓ false
[END: result=false]  [INCREMENT: divisor+1]
                            ↓
                [CONDITION: divisor < input?]
                   true ↓         ↓ false
              (volta pro MOD)  [END: result=true]
```

### Resultados esperados

| Input | É primo? | Resposta |
|---|---|---|
| 2 | sim | `{ "result": true }` |
| 4 | não | `{ "result": false }` |
| 7 | sim | `{ "result": true }` |
| 17 | sim | `{ "result": true }` |
| 100 | não | `{ "result": false }` |

---

## Decisões de arquitetura (resumo)

| Decisão | Alternativa descartada | Motivo da decisão |
|---|---|---|
| ID definido pelo cliente | Auto-gerado pelo banco | Permite referenciar blocos entre si em uma única requisição |
| `config` como JSON livre | Uma coluna por parâmetro | Schema flexível; novos tipos de bloco não alteram o banco |
| Strategy Pattern por tipo de bloco | `if/else` na engine | Cada executor tem uma única responsabilidade; adicionar bloco = nova classe, sem alterar a engine (SRP + Open/Closed) |
| Registry com injeção de lista | Switch manual | O Spring descobre os executores automaticamente; zero acoplamento |
| H2 (dev local) + Docker Compose (API + PostgreSQL) | PostgreSQL instalado localmente | Ambiente dev rápido com H2 via mvn spring-boot:run; ambiente completo com API + Postgres via docker compose up --build |
| `BaseEntity` com `@MappedSuperclass` | Repetir campos em cada entidade | `id`, `createdAt`, `updatedAt` em um só lugar |

---

## Estrutura de testes

```
backend/src/test/java/com/flownocode/api/
└── FlowNoCodeApplicationTests.java   → Smoke test: contexto do Spring sobe sem erros

backend/src/test/resources/
└── application-test.yml              → H2 com create-drop para testes isolados
```

O profile `test` usa H2 com `ddl-auto: create-drop` — o schema é criado antes de cada teste e destruído depois, garantindo isolamento total.

---

