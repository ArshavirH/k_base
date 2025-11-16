package com.buildware.kbase.knowledge.web;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import com.buildware.kbase.knowledge.service.KnowledgePersistenceService;
import com.buildware.kbase.knowledge.service.KnowledgeQueryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints to query the knowledge base. Provides a semantic search over vector-store chunks for a given project code
 * and free-text query.
 */
@RestController
@RequestMapping(path = "/knowledge", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeQueryService knowledgeQueryService;
    private final KnowledgePersistenceService persistenceService;
    private final KnowledgeApiMapper mapper;


    @GetMapping(path = "/search")
    public List<KnowledgeChunkDTO> query(
        @RequestParam String projectCode,
        @RequestParam String query,
        @RequestParam(required = false) Integer topK,
        @RequestParam(required = false) List<String> tags
    ) {
        int k = topK != null ? topK : 5;
        List<KnowledgeHit> hits = knowledgeQueryService.query(projectCode, query, k, tags);
        return mapper.toDtoList(hits);
    }

    /**
     * Ingest a document by splitting into chunks and persisting to the vector store.
     */
    @PostMapping(path = "/ingest", consumes = MediaType.APPLICATION_JSON_VALUE)
    public KnowledgeIngestResponseDTO ingest(@Valid @RequestBody KnowledgeIngestDTO req) {
        IngestDocument doc = mapper.toDomain(req);
        int count = persistenceService.ingestDocument(doc);
        return mapper.toIngestResponse(doc, count);
    }
}
