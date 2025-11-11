package com.buildware.kbase.knowledge.domain;

import java.util.List;
import java.util.Map;

/**
 * Domain ingesting text into the knowledge base.
 */
public record IngestDocument(
    String projectCode,
    String content,
    Map<String, String> metadata,
    List<String> tags
) {

}
