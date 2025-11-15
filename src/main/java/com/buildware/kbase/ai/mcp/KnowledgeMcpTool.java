package com.buildware.kbase.ai.mcp;

import com.buildware.kbase.spi.KnowledgeMaintenanceSPI;
import com.buildware.kbase.spi.KnowledgeMaintenanceSPI.IngestRequest;
import com.buildware.kbase.spi.KnowledgeMaintenanceSPI.IngestResponse;
import com.buildware.kbase.spi.KnowledgeSearchSPI;
import com.buildware.kbase.spi.KnowledgeSearchSPI.KnowledgeHitView;
import com.buildware.kbase.spi.KnowledgeSearchSPI.KnowledgeQuery;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * MCP tool adapter that exposes semantic knowledge queries to agents. Note: This uses Spring AI MCP annotations. If the
 * MCP runtime is enabled, this tool will be discoverable to MCP-compatible clients. Even without the runtime, this
 * adapter provides a stable seam and can be unit tested.
 */
@Component
@RequiredArgsConstructor
public class KnowledgeMcpTool {

    private final KnowledgeSearchSPI knowledgeSearchSPI;
    private final KnowledgeMaintenanceSPI maintenanceSPI;

    /**
     * Perform a semantic search within a project's knowledge base.
     *
     * @param input text input containing project code, text, and for topK results
     * @return list of ranked hits with text and metadata
     */
    @Tool(
        name = "knowledge.text",
        description = """
            Semantic search over a project's knowledge.
            Provide projectCode, text, optional topK.
             Returns ranked snippets with source to ground your answers.
            """
    )
    public List<KnowledgeHitView> semanticSearch(@Valid KnowledgeQuery input) {
        return knowledgeSearchSPI.semanticSearch(input);
    }

    @Tool(
        name = "knowledge.ingest",
        description = """
            Persist long-form text as project knowledge.
            Provide projectCode, content, optional metadata/tags.
            Chunks and embeds content for future semantic retrieval.
            """
    )
    public IngestResponse ingest(@Valid IngestRequest request) {
        return maintenanceSPI.ingest(request);
    }
}
