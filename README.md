# 🧠 MCP Knowledge Server

> Multi-project semantic knowledge-base server built with **Spring Boot**, **Spring AI**, and **pgvector**.
> Ingests Markdown and PDF documents, embeds them using OpenAI models, and exposes them via **MCP-compatible APIs** for AI agents.

---

## 🚀 Overview

The MCP Knowledge Server enables AI agents (or humans) to query rich project documentation — business plans, architecture docs, technical guides, etc. — semantically rather than by keywords.

It is designed to serve multiple projects (e.g. Cormit, Buildware, GiftBoxes) from a single instance.

---

## ⚙️ Features

✅ Multi-project knowledge domains
✅ Automatic ingestion of Markdown and PDF files
✅ Semantic search via Spring AI embeddings + pgvector
✅ MCP tool integration for AI agents (`@McpTool`)
✅ Configurable scheduler for auto-re-indexing
✅ Extensible design for Auth, Caching, and Admin UI

---

## 🧩 Architecture Snapshot

```
FileSystem → Parser → Embedding → pgvector → REST/MCP API → AI Agent
```

* **Ingestion Layer:** Reads Markdown/PDF files → chunks → embeds → stores in DB
* **Query Layer:** Receives user query → embeds query → semantic match against pgvector
* **MCP Layer:** Exposes protocol-compliant tools for agent consumption

See [`docs/architecture.md`](./docs/architecture.md) for detailed module layout and data flow.

---

## 📁 Project Structure

```
kbase/
├── README.md
├── AGENTS.md
├── docs/
│   ├── architecture.md
│   └── test-guidelines.md
├── local_stack/
│   └── docker-compose.yaml   # Local Postgres (pgvector)
├── src/
│   ├── main/java/com/buildware/kbase/...
│   └── test/java/com/buildware/kbase/...
└── build.gradle
```

---

## 🧱 Core Technologies

| Component        | Technology                                |
| ---------------- | ----------------------------------------- |
| Backend          | Spring Boot 3.3 ( Java 21 )               |
| AI Embeddings    | Spring AI (OpenAI text-embedding-3-large) |
| Database         | PostgreSQL + pgvector                     |
| Document Parsing | Flexmark (Markdown), Apache PDFBox (PDF)  |
| Protocol         | Model Context Protocol (MCP)              |
| Modularity       | Spring Modulith                           |
| Mapping          | MapStruct                                 |
| Boilerplate      | Lombok (compile-only)                     |
| Validation       | Jakarta Bean Validation (Spring)          |
| Utilities        | Apache Commons Lang                       |
| Testing Data     | Instancio                                |
| Migrations       | Flyway (SQL-based)                        |
| Container        | Docker + Compose                          |

---

## 🛠️ Setup & Run

### 1️⃣ Prerequisites

* Java 21
* PostgreSQL 16 + pgvector extension
* OpenAI API key

Enable pgvector:

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

### 2️⃣ Environment Variables

```bash
export OPENAI_API_KEY=sk-xxxx
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/kbase
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=user123
export MCP_KNOWLEDGE_DOCS_PATH=./knowledge
```

### 3️⃣ Run with Gradle

```bash
./gradlew bootRun
```

Server starts on `http://localhost:8080`.

Migrations: place SQL scripts under `src/main/resources/db/migration` (e.g., `V1__create_projects.sql`). Flyway runs on startup. Primary keys use UUIDs (`gen_random_uuid()`); ensure the `pgcrypto` extension is available.

---

## 📡 Key Endpoints

| Method | Path                   | Description                      |
| ------ | ---------------------- | -------------------------------- |
| `POST` | `/mcp/knowledge/add`   | Add a document manually          |
| `POST` | `/mcp/knowledge/query` | Semantic query by project/domain |
| `GET`  | `/mcp/projects`        | List available projects          |
| `GET`  | `/mcp/health`          | Health check                     |

Example:

```bash
curl -X POST http://localhost:8080/mcp/knowledge/query \
  -H "Content-Type: application/json" \
  -d '{"projectCode":"cormit","query":"Explain data flow architecture"}'
```

---

## 🤖 Development Agents

AI assistants collaborate on this codebase.
See [`AGENTS.md`](./AGENTS.md) for full roles and prompts.

| Agent           | Responsibility                     |
| --------------- | ---------------------------------- |
| architect-agent | Designs modules & data models      |
| codegen-agent   | Implements controllers & services  |
| data-agent      | Builds ingestion & embedding logic |
| docs-agent      | Maintains documentation            |
| mcp-agent       | Ensures MCP compliance             |
| qa-agent        | Tests & validates                  |
| devops-agent    | CI/CD & deployment                 |

---

## 🧠 Projects as Knowledge Domains

Each project has its own folder under `/knowledge` and is auto-discovered.

| Project   | Domain Tags                      |
| --------- | -------------------------------- |
| Cormit    | tech, implementation, monitoring |
| Buildware | business, devops, marketing      |
| LegalDocs | legal, workflow                  |
| GiftBoxes | design, marketing                |

---

## 🧩 MCP Integration

* Implements Model Context Protocol for tool exposure.
* Spring AI’s `@McpTool` annotation registers tools automatically.
* Agents can discover available tools via the manifest endpoint.

Example MCP tool:

```java
@McpTool(name="KnowledgeQuery",description="Query knowledge base by project and text")
public List<KnowledgeChunkResponse> invoke(String projectCode,String query){...}
```

---

## 🧪 Testing

```bash
./gradlew test
./gradlew jacocoTestReport  # generate coverage (HTML + XML)
```

Includes unit and integration tests for:

* Ingestion Pipeline
* Semantic Search
* Controller APIs
* MCP Tool Compliance

---

## 🐳 Docker Deployment (coming soon)

```bash
docker compose up -d
```

Services:

* `postgres` with pgvector (via `local_stack/docker-compose.yaml`)

---

## 📈 Roadmap

1. ✅ Core MVP — project-based ingestion + query
2. 🧩 Add scheduler for auto re-ingestion
3. 🔒 Integrate Auth layer (API keys / tokens)
4. 📊 Add observability (Prometheus + Grafana)
5. 🧠 Build React Admin Dashboard
6. ☁️ Deploy to AWS/Azure ECS/EKS

---

## 📚 Reference Docs

* [`AGENTS.md`](./AGENTS.md) – AI collaboration roles and prompts
* [`docs/architecture.md`](./docs/architecture.md) – System design and module blueprint
* [`docs/modulith.md`](./docs/modulith.md) – Spring Modulith setup and usage
* [`docs/coding-guidelines.md`](./docs/coding-guidelines.md) – Code style and conventions
* [`docs/test-guidelines.md`](./docs/test-guidelines.md) – Testing practices and tips

## 🔎 API Docs (Swagger)
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Optional Javadoc enrichment: enable Therapi by building with `-PenableTherapi`.

---
