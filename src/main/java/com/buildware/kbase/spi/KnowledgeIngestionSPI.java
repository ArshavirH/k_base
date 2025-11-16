package com.buildware.kbase.spi;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * SPI for knowledge base ingestion operations (e.g., document ingestion).
 */
public interface KnowledgeIngestionSPI {

    /**
     * Ingest a long text document into the project's knowledge base. Splits into chunks and persists to the vector
     * store.
     *
     * @param command ingest command payload
     * @return view containing summary data
     */
    KnowledgeIngestSummaryView ingest(KnowledgeIngestCommand command);

    /**
     * Command payload for ingestion.
     */
    record KnowledgeIngestCommand(
        @NotBlank String projectCode,
        @NotBlank String content,
        List<String> tags
    ) {

    }

    /**
     * Summary view returned after ingestion.
     */
    record KnowledgeIngestSummaryView(
        String projectCode,
        int ingestedChunks
    ) {

    }
}
