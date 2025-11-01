package com.buildware.kbase.project.web.dto;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

/**
 * Project representation exposed by the HTTP API.
 *
 * @param code        unique, URL-safe identifier for the project (e.g. {@code gift-boxes})
 * @param name        human-friendly name (e.g. {@code Gift Boxes})
 * @param basePath    absolute filesystem path backing this project
 * @param domainTags  optional domain tags used for organization and filtering
 * @param description optional free-form description of the project
 * @param visibility  visibility of the project; {@link VisibilityDTO#PUBLIC} by default
 * @param lastSyncAt  timestamp of the last successful filesystem synchronization
 */
@Builder
public record ProjectDTO(
    String code,
    String name,
    String basePath,
    List<String> domainTags,
    String description,
    VisibilityDTO visibility,
    Instant lastSyncAt
) {

}
