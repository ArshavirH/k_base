package com.buildware.kbase.knowledge.web.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * A single matching knowledge chunk returned from the search API.
 */
@Getter
@Setter
public class KnowledgeChunkResponse {

    /**
     * The text content of the chunk.
     */
    private String text;
    /**
     * The similarity score provided by the vector store.
     */
    private double score;
    /**
     * Source document path relative to the project base.
     */
    private String docPath;
    /**
     * Human-friendly title for the document, inferred from heading or filename.
     */
    private String title;
    /**
     * Zero-based index of this chunk within the source document.
     */
    private int chunkIndex;
}
