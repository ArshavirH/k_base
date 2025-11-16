# MCP Knowledge Server

> A persistent, semantic memory layer for AI agents and human teams.
> Built with Spring Boot, Spring AI, and pgvector.

---

## Overview

The **MCP Knowledge Server** provides a shared, intelligent knowledge base that allows AI agents and developers to **store, retrieve, and evolve project context** over time.

Instead of re-feeding long prompts or losing context between sessions, agents can query and update a central **semantic memory**, gaining true continuity and collaboration across tools, projects, and teams.

---

## Quick Start

- Prereqs: Java 21, Docker, Docker Compose, Node.js (for MCP inspector)
- Start Postgres (pgvector): `docker compose -f local_stack/docker-compose.yaml up -d`
- Build app: `./gradlew clean build`
- Run app: `./gradlew bootRun` (default profile: `server`)
- Swagger UI: http://localhost:8080/swagger-ui/index.html

---

## 🛠️ Project Setup

- Database:
  - Start local stack: `docker compose -f local_stack/docker-compose.yaml up -d`
  - Default connection: DB `kbase`, user `postgres`, password `user123`.
  - pgvector: ensure `CREATE EXTENSION IF NOT EXISTS vector;` exists (Docker stack includes it).
- Environment:
  - `OPENAI_API_KEY=...` (required for embeddings)
  - Optional overrides:
    - `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/kbase`
    - `SPRING_DATASOURCE_USERNAME=postgres`
    - `SPRING_DATASOURCE_PASSWORD=user123`
- Build & Verify:
  - Build: `./gradlew clean build`
  - Tests: `./gradlew test`
  - Lint: `./gradlew checkstyleMain checkstyleTest` (or `./gradlew check`)
  - Coverage: `./gradlew jacocoTestReport` → `build/reports/jacoco/test/html/index.html`
- Migrations:
  - Flyway SQLs live under `src/main/resources/db/migration` and run automatically on startup.

---

## ▶️ Run Locally

- Using Gradle (Server mode default): `./gradlew bootRun`
- Using Jar (Server mode default):
  - Build: `./gradlew clean build`
  - Run: `java -jar build/libs/kbase-*.jar`
- HTTP Port: `8080`
- OpenAPI (Swagger): `http://localhost:8080/swagger-ui/index.html`

To run in MCP mode (headless, no HTTP server):

- Using Gradle (MCP mode): `SPRING_PROFILES_ACTIVE=mcp ./gradlew bootRun`
- Using Jar (MCP mode): `java -jar build/libs/kbase-*.jar --spring.profiles.active=mcp`

---

## 🧪 Test via MCP Inspector

Use the MCP Inspector to exercise the server as an MCP tool host after building the jar. Pass the MCP profile so the app runs headless over stdio:

```
npx -y @modelcontextprotocol/inspector java -jar build/libs/kbase-*.jar --spring.profiles.active=mcp
```

This launches the Inspector UI connected to the running Spring Boot MCP server (stdio transport), allowing you to invoke tools (e.g., `knowledge.text`, `knowledge.ingest`) interactively.

### MCP Client Configuration Example

For MCP-compatible clients that use a static configuration file (example JSON), point to the Java command with the MCP profile and stdio transport:

```json
{
  "mcpServers": {
    "kbase": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/kbase-*.jar",
        "--spring.profiles.active=mcp"
      ],
      "transport": "stdio"
    }
  }
}
```

Note: Ensure the PostgreSQL database with pgvector is running and reachable before connecting via MCP. For local development, start the stack with `docker compose -f local_stack/docker-compose.yaml up -d`, or point `SPRING_DATASOURCE_*` env vars to an available database.

---

## MCP Tools

- `knowledge.text`: Semantic search over a project's knowledge. Provide `projectCode`, `text`, optional `topK`, optional metadata filters, optional `tags`. Returns ranked snippets with source to ground your answers. Filters match ALL provided metadata key/value pairs; tags require all requested values to be present in the document's `tags` metadata.
- `knowledge.ingest`: Persist long-form text as project knowledge. Provide `projectCode`, `content`, optional `metadata/tags`. Chunks and embeds content for future semantic retrieval.

These tools are discoverable by MCP-compatible clients when the server is running. Use them to read from and write to the shared semantic memory layer during multi-step tasks.

---

## ⚙️ Configuration

| Key                                  | How to set (env)                 | Notes                             |
| ------------------------------------ | --------------------------------- | --------------------------------- |
| `spring.ai.openai.api-key`           | `OPENAI_API_KEY`                  | Required for embeddings           |
| `spring.ai.vector-store.pgvector.*`  | env overrides supported           | Dimensions default to `1536`      |
| `spring.profiles.active=server`      | set via env or `--args`           | Enables HTTP server + Swagger     |
| `spring.profiles.active=mcp`         | set via env or `--args`           | Headless MCP over stdio           |
| `spring.profiles.default=server`     | set in `application.yaml`         | Default profile for runs without overrides |

---

## Project Technologies

- Java 21 + Spring Boot 3.x
- Spring AI (embeddings, vector operations)
- PostgreSQL + pgvector for semantic storage
- Flyway for database migrations (SQL-first)
- Spring Modulith for modular boundaries and SPI ports
- MapStruct for DTO ↔ domain mapping
- Lombok for boilerplate reduction
- Bean Validation (`spring-boot-starter-validation`)
- Springdoc OpenAPI (Swagger UI)
- Gradle build with Checkstyle and JaCoCo

---

## 🧑‍💻 Dev Commands

- Build: `./gradlew clean build`
- Run: `./gradlew bootRun`
- Tests: `./gradlew test`
- Lint: `./gradlew checkstyleMain checkstyleTest`
- Coverage: `./gradlew jacocoTestReport` → `build/reports/jacoco/test/html/index.html`

---

##  Core Purpose

| Goal                        | Description                                                                           |
| --------------------------- | ------------------------------------------------------------------------------------- |
| **Long-term Memory**        | Persist domain knowledge, rules, and context beyond one conversation or session.      |
| **On-Demand Retrieval**     | Agents fetch only what’s relevant through semantic search — no token overload.        |
| **Context Persistence**     | Agents can write back new findings, summaries, or decisions to enrich the database.   |
| **Unified Knowledge Layer** | Integrate Jira issues, GitHub PRs, docs, and notes into one semantic space.           |
| **Multi-Project Support**   | Each project (e.g., *Cormit*, *Buildware*, *GiftBoxes*) has its own knowledge domain. |

---

## Concept Diagram

```plaintext
 ┌──────────────────────────┐
 │    Human / AI Agent      │
 │ (Copilot, Codex, Claude) │
 └─────────────┬────────────┘
               │
  MCP Protocol (Streamable HTTP)
               │
               ▼
 ┌───────────────────────────────┐
 │     MCP Knowledge Server      │
 │  - knowledge.text(project,q)  │
 │  - knowledge.ingest(project,c)│
 └─────────────┬─────────────────┘
               │
 ┌─────────────┴─────────────────┐
 │        pgvector DB            │
 │  (docs, embeddings, memory)   │
 └───────────────────────────────┘
```

---

## ⚙️ How It Works

1. **Ingestion** – Markdown, PDFs, Jira tickets, and PRs are parsed and embedded into pgvector.
2. **Retrieval** – Agents call `knowledge.text` to fetch semantically matched data, grounded by source.
3. **Persistence** – Agents call `knowledge.ingest` to write summaries, insights, and notes back to memory (include tags under metadata as needed).
4. **Synchronization** – External project data stays up to date through periodic sync (planned connectors).

MCP-enabled agents share the same memory:
- Any MCP-compatible agent can connect (IDE copilots, terminal/CLI assistants, chat UIs, research/planning agents) — not just coding agents.
- All connected agents read/write via the same MCP tools (`knowledge.text`, `knowledge.ingest`), sharing one project-scoped semantic memory across tools.

---

## Agent Capabilities

| Action              | Description                                                                    |
| ------------------- | ------------------------------------------------------------------------------ |
| `knowledge.text`    | Retrieve relevant project knowledge for reasoning and coding tasks with source.|
| `knowledge.ingest`  | Write summaries, decisions, and documents back to project memory.              |
| `syncSources` (WIP) | Re-index project data from connected systems (Jira, GitHub, Docs).             |

Agents can collaborate around the same persistent memory, sharing domain-specific vocabulary, design rules, and task knowledge.

---

## Example Use Cases

* **Collaboration & Multi-Agent:** Multiple agents, applications, and chat sessions work against the same
  project knowledge base to enable coordinated workflows, handoffs, and centralized memory management.
* **Development:** Codex agent learns project conventions and architectures.
* **Documentation:** Docs agent updates long-term knowledge after changes.
* **PM Tools:** Jira or GitHub data flows into the shared memory automatically.
* **Research/Analysis:** Agents correlate tickets, commits, and docs to summarize progress.

---

## 💬 Summary

The **MCP Knowledge Server** bridges the gap between *ephemeral AI prompts* and *persistent organizational memory*.
It allows agents to **think with context, learn over time**, and **collaborate across projects** through a unified, semantic knowledge base.
