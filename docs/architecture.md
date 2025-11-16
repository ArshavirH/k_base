# 🏗️ Architecture Overview

> Project: `kbase` — Knowledge base server for multi-project AI agents (Spring Boot + Spring AI + pgvector)

This document reflects the current implementation in the repository. It covers runtime profiles (Server vs MCP), module boundaries, data flows, and public SPI contracts.

---

## 1) System Components

| Layer   | Description                                                | Key Packages |
| ------- | ---------------------------------------------------------- | ------------ |
| Core    | Projects catalog; knowledge query + persistence services   | `project`, `knowledge` |
| API     | REST controllers + DTO mapping                             | `project.web`, `knowledge.web` |
| Config  | Spring profiles, OpenAPI, management                       | `src/main/resources`, `config` |
| SPI     | Cross-module contracts (records for views/commands)        | `spi` (`ProjectInfoSPI`, `KnowledgeSearchSPI`, `KnowledgeIngestionSPI`) |
| MCP     | Spring AI MCP server + tools (stdio transport)             | `ai.mcp` |

Spring Modulith annotations in `package-info.java` document module boundaries; feature modules implement their own SPIs. See `docs/modulith.md`.

---

## 2) Package Layout (selected)

```
com.buildware.kbase
├── Application.java
├── config/
│   └── OpenApiConfig.java
├── project/
│   ├── domain/ Project.java
│   ├── repository/ ProjectRepository.java
│   ├── service/ ProjectService.java
│   └── web/ ProjectController.java
├── knowledge/
│   ├── domain/ IngestDocument.java (record)
│   ├── mapper/ DocumentChunkMapper.java, KnowledgeIngestionMapper.java
│   ├── service/
│   │   ├── KnowledgeQueryService.java
│   │   ├── KnowledgeSearchSPIImpl.java (implements KnowledgeSearchSPI)
│   │   ├── KnowledgePersistenceService.java
│   │   └── KnowledgePersistenceSPIImpl.java (implements KnowledgeIngestionSPI)
│   └── web/ KnowledgeController.java, KnowledgeIngestDTO.java, KnowledgeIngestResponseDTO.java
└── spi/
    ├── ProjectInfoSPI.java
    ├── KnowledgeSearchSPI.java
    └── KnowledgeIngestionSPI.java
```

Additional adapters

```
com.buildware.kbase.ai.mcp
├── KnowledgeMcpTool.java
└── MCPServerToolsRegistrar.java
```

---

## 3) Data Flow

### Query (User → Results)

```
Client → POST /knowledge/query (projectCode, query, topK)
  ↓
VectorStore.similaritySearch(filter by projectCode)
  ↓
DTO mapping (text, score, docPath, title, chunkIndex)

### Ingestion (Text → Chunks → VectorStore)

```
Client (REST or MCP `knowledge.ingest`) → KnowledgeController/KnowledgeMcpTool
  ↓
KnowledgeIngestionSPI.ingest(KnowledgeIngestCommand)
  ↓  (module adapter)
KnowledgePersistenceService.ingestDocument(IngestDocument)
  ↓
DocumentChunkMapper.toDocuments(...) → VectorStore.add(List<Document>)
  ↓
KnowledgeIngestSummaryView (projectCode, ingestedChunks)
```
```

---

## 4) Endpoints & MCP Tools

- `POST /knowledge/query` — semantic search
- `POST /knowledge/ingest` — ingest long-form text into project knowledge
- `GET /projects` — list projects (optionally include confidential)
- `GET /projects/{code}` — get project by code

MCP Tools (stdio):
- `knowledge.text` — semantic search (supports metadata filters and tags)
- `knowledge.ingest` — persist long-form text (supports metadata/tags)

Base route prefixes: `/knowledge` and `/projects`.

OpenAPI/Swagger is available at `/swagger-ui/index.html`.

---

## 5) Configuration

| Key                        | Description                           | Default/Notes                     |
| -------------------------- | ------------------------------------- | --------------------------------- |
| `spring.ai.openai.api-key` | API key for embeddings                | `OPENAI_API_KEY`                  |
| `spring.ai.vector-store.pgvector.dimensions` | Embedding dimensions          | `1536` (text-embedding-3-small)   |
| `server.port`              | HTTP port                             | `8080`                            |
| `spring.profiles.default`  | Default runtime profile               | `server`                          |

Flyway SQL migrations live under `src/main/resources/db/migration`.

---

## 6) Runtime Profiles

- Server Profile (`application-server.yaml`)
  - `web-application-type: servlet`, Swagger + Actuator enabled, INFO logs, MCP server disabled.
- MCP Profile (`application-mcp.yaml`)
  - `web-application-type: none`, banner off, quiet logs, Swagger off, MCP server enabled over stdio.
- Base (`application.yaml`)
  - Shared DB, vector, and OpenAI config; `spring.profiles.default=server`.

## 🧩 **8. SPI & Extension Points**

| Area                      | Description                                                |
| ------------------------- | ---------------------------------------------------------- |
| KnowledgeSearchSPI        | Query SPI used by external modules and MCP tools          |
| KnowledgeIngestionSPI     | Command SPI to ingest long-form documents                 |
| ProjectInfoSPI            | Project lookup used by knowledge services                 |
| Auth Layer                | Add API key/JWT for agent access                          |
| Cache                     | Redis for embedding/result caching                        |
| Analytics                 | Metrics: query volume, latency, recall                    |
| Admin UI                  | Lightweight dashboard for management                      |

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
