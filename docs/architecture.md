# 🏗️ **Architecture Overview**

> Version: 1.0
> Project: `kbase`
> Purpose: Knowledge base server for multi-project AI agents (Spring Boot + Spring AI + pgvector)

Note: The current repository bootstraps a Spring Boot app; the module layout below reflects the target design and will be introduced iteratively.

---

## 🧱 **1. System Components**

| Layer                    | Description                                           | Key Modules                        |
| ------------------------ | ----------------------------------------------------- | ---------------------------------- |
| **Core Layer**           | Handles core entities, repositories, and data model   | `project`, `knowledge`, `chunk`    |
| **AI Layer**             | Embeddings, semantic search, and MCP tool integration | `ai`, `embedding`, `mcp`           |
| **Ingestion Layer**      | Parses Markdown/PDF, chunks text, and saves data      | `ingestion`, `parser`, `scheduler` |
| **API Layer**            | Exposes REST + MCP endpoints for agents               | `controller`, `dto`, `config`      |
| **Infrastructure Layer** | PostgreSQL + pgvector integration, Docker, CI/CD      | `infrastructure`, `devops`         |
| **Docs & Config**        | Markdown docs, configuration YAML, and metadata       | `/docs`, `/knowledge`, `/config`   |

---

## 🧩 **2. Package Layout**

This project adopts Spring Modulith. Each top-level feature forms a module (package) annotated via `package-info.java` with `@ApplicationModule`. Modules communicate via well-defined interfaces or events and should not depend cyclically on each other. See `docs/modulith.md` for patterns and tests.

```
com.buildware.kbase
├── McpKnowledgeServerApplication.java
│
├── project/
│   ├── Project.java
│   ├── ProjectRepository.java
│   ├── ProjectService.java
│   └── ProjectController.java
│
├── knowledge/
│   ├── KnowledgeDocument.java
│   ├── KnowledgeChunk.java
│   ├── KnowledgeDocumentRepository.java
│   ├── KnowledgeChunkRepository.java
│   └── KnowledgeQueryService.java
│
├── ingestion/
│   ├── FileIngestionService.java
│   ├── MarkdownParser.java
│   ├── PdfParser.java
│   ├── IngestionScheduler.java
│   └── IngestionUtils.java
│
├── ai/
│   ├── EmbeddingService.java
│   ├── EmbeddingUtils.java
│   └── SemanticSearchService.java
│
├── mcp/
│   ├── KnowledgeMcpTool.java
│   ├── McpToolRegistry.java
│   ├── McpConfig.java
│   └── McpHealthController.java
│
├── controller/
│   ├── KnowledgeController.java
│   ├── ProjectController.java
│   └── HealthController.java
│
├── config/
│   ├── DatabaseConfig.java
│   ├── AiConfig.java
│   ├── ApplicationProperties.java
│   └── LoggingConfig.java
│
└── dto/
    ├── KnowledgeAddRequest.java
    ├── KnowledgeQueryRequest.java
    ├── KnowledgeChunkResponse.java
    ├── ProjectResponse.java
    └── DomainResponse.java
```

---

## 🧠 **3. Module Responsibilities**

### 🏗️ Core: `project` & `knowledge`

* Define entities (`Project`, `KnowledgeDocument`, `KnowledgeChunk`).
* Manage relationships and indexing logic.
* Provide repositories for CRUD + metadata filtering.
* Handle `pgvector` persistence and search queries.

**Agent Owner:** `architect-agent`, `codegen-agent`

---

### 🧩 AI Layer: `ai`

* Handles all **embedding** generation (Spring AI).
* Provides **semantic search service** using vector distance (`<=>` operator).
* Abstracts model provider (OpenAI now, extendable to Anthropic, Mistral, etc.).
* Provides embedding batching and retry strategies.

**Agent Owner:** `mcp-agent`, `data-agent`

---

### 📥 Ingestion Layer: `ingestion`

* Reads from `/knowledge/{projectCode}/`.
* Extracts text from Markdown and PDF.
* Splits into ~500-word chunks with context preservation.
* Embeds text chunks and saves to DB.
* Includes `IngestionScheduler` (e.g., cron: every 6h).

**Agent Owner:** `data-agent`

---

### 🌐 API Layer: `controller`

* Provides REST + MCP endpoints:

    * `/mcp/knowledge/query`
    * `/mcp/knowledge/add`
    * `/mcp/projects/list`
    * `/mcp/health`
* JSON input/output + schema validation.
* Returns ordered semantic results with metadata.

**Agent Owner:** `codegen-agent`, `mcp-agent`

---

### 🧩 MCP Integration: `mcp`

* Registers tools annotated with `@McpTool`.
* Exposes MCP manifest for discovery by agents.
* Validates tool schema and parameters.
* Handles `/mcp/manifest.json` endpoint.

**Agent Owner:** `mcp-agent`

---

### 🛠️ Infrastructure: `config`, `infrastructure`

* Database & AI configuration (pgvector, Spring AI).
* Profiles: `dev`, `test`, `prod`.
* Logging, CORS, and exception handling.
* Dockerfile, Compose, and pipeline configs.

**Agent Owner:** `devops-agent`

---

## 🧾 **4. Data Flow**

### Ingestion Flow

```
FileSystem (Markdown/PDF)
    ↓
Parser (Flexmark / PDFBox)
    ↓
IngestionService (chunks + embeddings)
    ↓
pgvector DB (documents + embeddings)
```

Schema changes are managed via Flyway SQL migrations residing in `src/main/resources/db/migration`. Migrations run automatically on application startup.

### Query Flow

```
Client / Agent → /mcp/knowledge/query
    ↓
EmbeddingService → OpenAI API (Spring AI)
    ↓
SemanticSearchService → pgvector <=> queryEmbedding
    ↓
Response (ranked chunks + metadata)
```

---

## 🔌 **5. Configuration Conventions**

| Key                           | Description                       | Example                                  |
| ----------------------------- | --------------------------------- | ---------------------------------------- |
| `mcp.knowledge.docs-path`     | Root path for project directories | `./knowledge`                            |
| `mcp.knowledge.scan-interval` | Scheduler interval                | `6h`                                     |
| `spring.ai.openai.api-key`    | API key for embeddings            | `${OPENAI_API_KEY}`                      |
| `spring.datasource.url`       | JDBC connection string            | `jdbc:postgresql://localhost:5432/mcpdb` |

---

## 🧩 **6. Project Directory Convention**

```
knowledge/
 ├── cormit/
 │   ├── architecture.md
 │   ├── implementation.pdf
 │   └── business_overview.md
 ├── buildware/
 │   ├── tech_stack.md
 │   └── marketing_strategy.md
 ├── legaldocs/
 │   └── data_retention_policy.pdf
 └── giftboxes/
     └── design_guide.md
```

Each folder maps to one `Project` entity.
Each file is a document automatically ingested and indexed.

---

## 🧠 **7. Development Rules for AI Agents**

| Agent             | Responsibility                              | Output                             |
| ----------------- | ------------------------------------------- | ---------------------------------- |
| `architect-agent` | Maintain architecture.md and data models    | UML diagrams, entity relationships |
| `codegen-agent`   | Write or refactor Java classes per spec     | Source code, DTOs, controllers     |
| `data-agent`      | Manage ingestion logic, handle file parsing | Markdown/PDF parser, scheduler     |
| `docs-agent`      | Maintain docs, update README/setup guides   | Markdown docs                      |
| `mcp-agent`       | Ensure tool compliance, manifest generation | MCP descriptors                    |
| `qa-agent`        | Write and validate tests                    | JUnit classes, coverage reports    |
| `devops-agent`    | Configure Docker, CI/CD, cloud deploy       | Dockerfile, workflow YAMLs         |

---

## 🧩 **8. Extension Points**

| Area           | Description                                   |
| -------------- | --------------------------------------------- |
| **Auth Layer** | Add API key or JWT for agent access           |
| **Versioning** | Version per document and project release      |
| **Cache**      | Add Redis for embedding result caching        |
| **Analytics**  | Add metrics: query volume, latency, recall    |
| **Admin UI**   | Lightweight React/Next.js dashboard (Phase 3) |

---

## 🚀 **9. Milestones**

| Phase               | Goal                                        | Deliverables                       |
| ------------------- | ------------------------------------------- | ---------------------------------- |
| **1. Core Setup**   | Basic Spring Boot app, pgvector integration | Entities, repos, config            |
| **2. Ingestion**    | Markdown/PDF ingestion + embeddings         | IngestionService, EmbeddingService |
| **3. Query API**    | Semantic search endpoint                    | Query controller, pgvector query   |
| **4. MCP Tools**    | Spring AI MCP tool exposure                 | `@McpTool` classes                 |
| **5. Scheduler**    | Auto re-ingestion + project discovery       | `IngestionScheduler`               |
| **6. Docs & Admin** | Docs ingestion, admin API, dashboard        | Docs + optional UI                 |

---
### Data Model Notes

- Primary keys are UUIDs (generated via `gen_random_uuid()` in PostgreSQL).
- Enumerations are stored as strings (e.g., `visibility` uses VARCHAR).
