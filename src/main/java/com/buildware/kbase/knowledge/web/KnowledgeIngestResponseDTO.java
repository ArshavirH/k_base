package com.buildware.kbase.knowledge.web;

/**
 * Response payload returned after a successful ingest operation.
 *
 * @param projectCode    project identifier used for the ingest
 * @param ingestedChunks number of vector chunks produced and stored for the content
 */
public record KnowledgeIngestResponseDTO(
    String projectCode,
    int ingestedChunks
) {

}
