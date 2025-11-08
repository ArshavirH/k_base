package com.buildware.kbase.knowledge.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response containing the results of a knowledge synchronization operation.
 */
@Getter
@AllArgsConstructor
public class KnowledgeSyncResponse {
    /**
     * Code of the project that was synchronized.
     */
    private String projectCode;
    /**
     * Number of source documents processed.
     */
    private int documentsProcessed;
    /**
     * Number of chunks written to the vector store.
     */
    private int chunksProcessed;
}
