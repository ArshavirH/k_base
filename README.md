# 🧠 MCP Knowledge Server

> A persistent, semantic memory layer for AI agents and human teams.
> Built with Spring Boot, Spring AI, and pgvector.

---

## 🌍 Overview

The **MCP Knowledge Server** provides a shared, intelligent knowledge base that allows AI agents and developers to **store, retrieve, and evolve project context** over time.

Instead of re-feeding long prompts or losing context between sessions, agents can query and update a central **semantic memory**, gaining true continuity and collaboration across tools, projects, and teams.

---

## 💡 Core Purpose

| Goal                        | Description                                                                           |
| --------------------------- | ------------------------------------------------------------------------------------- |
| **Long-term Memory**        | Persist domain knowledge, rules, and context beyond one conversation or session.      |
| **On-Demand Retrieval**     | Agents fetch only what’s relevant through semantic search — no token overload.        |
| **Context Persistence**     | Agents can write back new findings, summaries, or decisions to enrich the database.   |
| **Unified Knowledge Layer** | Integrate Jira issues, GitHub PRs, docs, and notes into one semantic space.           |
| **Multi-Project Support**   | Each project (e.g., *Cormit*, *Buildware*, *GiftBoxes*) has its own knowledge domain. |

---

## 🧩 Concept Diagram

```plaintext
 ┌──────────────────────────┐
 │    Human / AI Agent      │
 │ (Copilot, Codex, Claude) │
 └─────────────┬────────────┘
               │
        MCP Protocol (Streamable HTTP)
               │
               ▼
 ┌──────────────────────────────┐
 │     MCP Knowledge Server      │
 │  - queryKnowledge(project,q)  │
 │  - persistContext(project,c)  │
 │  - syncSources(project)       │
 └─────────────┬────────────────┘
               │
 ┌─────────────┴──────────────┐
 │        pgvector DB         │
 │  (docs, embeddings, memory)│
 └─────────────┬──────────────┘
               │
     ┌────────────────────────┐
     │  External Sources (API) │
     │  Jira, GitHub, Docs     │
     └────────────────────────┘
```

---

## ⚙️ How It Works

1. **Ingestion** – Markdown, PDFs, Jira tickets, and PRs are parsed and embedded into pgvector.
2. **Retrieval** – Agents use MCP tools (`queryKnowledge`) to fetch semantically matched data.
3. **Persistence** – Agents store new insights (`persistContext`) to extend the knowledge base.
4. **Synchronization** – External project data stays up to date through periodic sync.

---

## 🧠 Agent Capabilities

| Action           | Description                                                        |
| ---------------- | ------------------------------------------------------------------ |
| `queryKnowledge` | Retrieve relevant project knowledge for reasoning or coding tasks. |
| `persistContext` | Write summaries, explanations, or decisions back to memory.        |
| `syncSources`    | Re-index project data from connected systems (Jira, GitHub, etc.). |

Agents can collaborate around the same persistent memory, sharing domain-specific vocabulary, design rules, and task knowledge.

---

## 🌱 Example Use Cases

* **Development:** Codex agent learns project conventions and architectures.
* **Documentation:** Docs agent updates long-term knowledge after changes.
* **PM Tools:** Jira or GitHub data flows into the shared memory automatically.
* **Research/Analysis:** Agents correlate tickets, commits, and docs to summarize progress.

---

## 🔮 Vision & Roadmap

| Phase           | Focus                                                  |
| --------------- | ------------------------------------------------------ |
| **MVP**         | Semantic ingestion + query by project                  |
| **Next**        | Agent write-back for persistent context                |
| **Integration** | Jira / GitHub / Slack sync connectors                  |
| **Evolution**   | Automatic summarization & long-term memory compression |
| **UX**          | Admin & observability dashboard                        |
| **Future**      | Secure multi-tenant access & fine-grained auth         |

---

## 💬 Summary

The **MCP Knowledge Server** bridges the gap between *ephemeral AI prompts* and *persistent organizational memory*.
It allows agents to **think with context, learn over time**, and **collaborate across projects** through a unified, semantic knowledge base.

> It’s not just retrieval — it’s the foundation of *continuous, contextual intelligence* for every AI-driven workflow.
