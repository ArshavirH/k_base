package com.buildware.kbase.knowledge.web;

public record KnowledgeIngestResponseDTO(
    String projectCode,
    int ingestedChunks
) {

}
