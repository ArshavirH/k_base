package com.buildware.kbase.knowledge.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Knowledge query DTO payload.
 */
@Getter
@Setter
public class KnowledgeQueryDTO {

    @NotBlank
    private String projectCode;

    @NotBlank
    private String query;

    @Min(1)
    private Integer topK;
}
