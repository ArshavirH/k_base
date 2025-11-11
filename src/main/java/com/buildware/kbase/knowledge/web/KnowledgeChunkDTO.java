package com.buildware.kbase.knowledge.web;

/**
 * A single matching knowledge chunk returned from the search API.
 */
public record KnowledgeChunkDTO(
    String text,
    double score,
    String docPath,
    String title,
    int chunkIndex
) {}
