package com.buildware.kbase.knowledge.domain;

import java.util.List;

/**
 * Domain ingesting text into the knowledge base.
 */
public record IngestDocument(
    String projectCode,
    String content,
    List<String> tags
) {

}
