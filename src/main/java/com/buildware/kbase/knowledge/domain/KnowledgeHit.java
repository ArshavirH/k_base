package com.buildware.kbase.knowledge.domain;

public record KnowledgeHit(
    String text,
    double score,
    String docPath,
    String title,
    int chunkIndex
) {

}
