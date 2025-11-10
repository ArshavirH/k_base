package com.buildware.kbase.spi;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * SPI for semantic knowledge queries across modules.
 */
public interface KnowledgeSearchSPI {

    /**
     * Query knowledge scoped to a project.
     *
     * @param query text for project knowledge
     * @return ranked list of view results
     */
    List<KnowledgeHitView> semanticSearch(KnowledgeQuery query);

    record KnowledgeQuery(
        @NotBlank String projectCode,
        @NotBlank String text,
        Integer topK
    ) {

    }

    record KnowledgeHitView(
        String text,
        Double score,
        String title,
        String docPath,
        Integer chunkIndex
    ) {

    }
}


