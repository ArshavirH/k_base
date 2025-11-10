# 🏗️ Architecture Overview

> Project: `kbase` — Knowledge base server for multi-project AI agents (Spring Boot + Spring AI + pgvector)

This document reflects the current implementation in the repository.

---

## 1) System Components

| Layer          | Description                                             | Key Packages                      |
| -------------- | ------------------------------------------------------- | --------------------------------- |
| Core           | Projects catalog + vector search services               | `project`, `knowledge`            |
| Sync           | Filesystem scan, chunking (TokenTextSplitter), upserts | `knowledge.service`               |
| API            | REST controllers + DTO mapping                          | `project.web`, `knowledge.web`    |
| Config         | OpenAPI, CORS, knowledge path config                    | `config`                          |
| SPI            | Cross-module contracts (views nested)                   | `spi` (`ProjectInfoSPI`, `KnowledgeSearchSPI`) |

Spring Modulith annotations in `package-info.java` document module boundaries. See `docs/modulith.md`.

---

## 2) Package Layout (selected)

```
com.buildware.kbase
├── Application.java
├── config/
│   ├── OpenApiConfig.java
│   └── KnowledgeProperties.java        # binds mcp.knowledge.docs-path
├── project/
│   ├── domain/ Project.java
│   ├── repository/ ProjectRepository.java
│   ├── service/ ProjectService.java
│   └── web/ ProjectController.java
├── knowledge/
│   ├── service/ KnowledgeQueryService.java, KnowledgeSyncService.java, KnowledgeSearchSPIImpl.java
│   └── web/ KnowledgeController.java, KnowledgeApiMapper.java
└── spi/
    ├── ProjectInfoSPI.java
    └── KnowledgeSearchSPI.java
```

Additional adapters

```
com.buildware.kbase.ai.mcp
├── KnowledgeMcpTool.java
└── MCPServerToolsRegistrar.java
```

---

## 3) Data Flow

### Sync (Filesystem → Vector Store)

```
Project base path
   ↓ walk + filter (*.md, *.markdown, *.txt)
TokenTextSplitter → chunks + metadata
   ↓ embeddings via Spring AI (OpenAI)
pgvector (vector_store table)
```

`KnowledgeSyncService` prevents duplicate loads using a per-document marker record (content hash) stored in the vector store.

### Query (User → Results)

```
Client → POST /knowledge/query (projectCode, query, topK)
  ↓
VectorStore.similaritySearch(filter by projectCode)
  ↓
DTO mapping (text, score, docPath, title, chunkIndex)
```

---

## 4) Endpoints

- `POST /knowledge/query` — semantic search
- `POST /knowledge/sync` — sync all projects
- `POST /knowledge/sync/{projectCode}` — sync one project
- `GET /projects` — list projects (optionally include confidential)
- `GET /projects/{code}` — get project by code
- `POST /projects/sync` — discover projects from knowledge path

Base route prefixes: `/knowledge` and `/projects`.

OpenAPI/Swagger is available at `/swagger-ui/index.html`.

---

## 5) Configuration

| Key                        | Description                           | Default/Notes                     |
| -------------------------- | ------------------------------------- | --------------------------------- |
| `mcp.knowledge.docs-path`  | Root path for project directories     | Set via env `MCP_KNOWLEDGE_DOCS_PATH` |
| `spring.ai.openai.api-key` | API key for embeddings                | `OPENAI_API_KEY`                  |
| `spring.ai.vector-store.pgvector.dimensions` | Embedding dimensions          | `1536` (text-embedding-3-small)   |
| `server.port`              | HTTP port                             | `8080`                            |

Flyway SQL migrations live under `src/main/resources/db/migration`.

---

## 6) Knowledge Directory Convention

```
knowledge/
 ├── cormit/
 │   ├── architecture.md
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
