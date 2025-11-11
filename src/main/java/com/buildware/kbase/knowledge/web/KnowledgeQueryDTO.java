package com.buildware.kbase.knowledge.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Knowledge query DTO payload.
 */
public record KnowledgeQueryDTO(
    @NotBlank String projectCode,
    @NotBlank String query,
    @Min(1) Integer topK
) {

}
