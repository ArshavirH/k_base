package com.buildware.kbase.knowledge.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for the knowledge query endpoint.
 */
@Getter
@Setter
public class KnowledgeQueryRequest {

    @NotBlank
    private String projectCode;

    @NotBlank
    private String query;

    @Min(1)
    private Integer topK;
}
