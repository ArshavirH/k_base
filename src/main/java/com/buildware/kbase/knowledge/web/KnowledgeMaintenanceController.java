package com.buildware.kbase.knowledge.web;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.knowledge.service.KnowledgeMaintenanceService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Knowledge maintenance endpoints for ingesting long documents into the vector store.
 */
@RestController
@RequestMapping(path = "/knowledge", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@lombok.RequiredArgsConstructor
public class KnowledgeMaintenanceController {

    private final KnowledgeMaintenanceService maintenanceService;
    private final KnowledgeApiMapper mapper;

    /**
     * Ingest a document by splitting into chunks and persisting to the vector store.
     */
    @PostMapping(path = "/ingest", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KnowledgeIngestResponseDTO> ingest(@Valid @RequestBody KnowledgeIngestDTO req) {
        IngestDocument doc = mapper.toDomain(req);
        var count = maintenanceService.ingestDocument(doc);
        return ResponseEntity
            .status(201)
            .body(mapper.toIngestResponse(doc, count));
    }
}
