package com.buildware.kbase.spi;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * SPI for knowledge base maintenance operations (e.g., document ingestion).
 */
public interface KnowledgeMaintenanceSPI {

    /**
     * Ingest a long text document into the project's knowledge base.
     * Splits into chunks and persists to the vector store.
     *
     * @param request ingest payload
     * @return response containing summary data
     */
    IngestResponse ingest(IngestRequest request);

    /**
     * View type for ingest request.
     */
    record IngestRequest(
        @NotBlank String projectCode,
        @NotBlank String content,
        Map<String, String> metadata,
        List<String> tags
    ) {}

    /**
     * View type for ingest response.
     */
    record IngestResponse(
        String projectCode,
        int ingestedChunks
    ) {}
}

