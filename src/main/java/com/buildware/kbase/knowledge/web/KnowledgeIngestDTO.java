package com.buildware.kbase.knowledge.web;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Ingest request payload for adding long-form text to a project's knowledge base.
 * The server chunks and embeds the provided content and persists it to the vector store.
 * Metadata is derived internally; clients should only provide project and optional tags.
 *
 * @param projectCode unique project identifier to scope the knowledge
 * @param content     full text content to ingest (will be chunked internally)
 * @param tags        optional labels to categorize the document for later filtering
 */
public record KnowledgeIngestDTO(
    @NotBlank String projectCode,
    @NotBlank String content,
    List<String> tags
) {

}
