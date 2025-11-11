package com.buildware.kbase.knowledge.web;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public record KnowledgeIngestDTO(
    @NotBlank String projectCode,
    @NotBlank String content,
    Map<String, String> metadata,
    List<String> tags
) {

}
