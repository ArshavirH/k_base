package com.buildware.kbase.knowledge.web;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import com.buildware.kbase.knowledge.service.KnowledgeQueryService;
import com.buildware.kbase.knowledge.service.KnowledgeSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing endpoints to text and synchronize the knowledge base. Endpoints allow clients to: - Query
 * vector-store backed chunks for a given project and free-text text. - Trigger synchronization of documents from a
 * project base path into the vector store.
 */
@RestController
@RequestMapping(path = "/knowledge", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "Knowledge", description = "Query and synchronize the knowledge base")
public class KnowledgeController {

    private final KnowledgeQueryService knowledgeQueryService;
    private final KnowledgeSyncService knowledgeSyncService;
    private final KnowledgeApiMapper mapper;

    public KnowledgeController(
        KnowledgeQueryService knowledgeQueryService,
        KnowledgeSyncService knowledgeSyncService,
        KnowledgeApiMapper mapper
    ) {
        this.knowledgeQueryService = knowledgeQueryService;
        this.knowledgeSyncService = knowledgeSyncService;
        this.mapper = mapper;
    }

    /**
     * Executes a semantic search over knowledge chunks for a specific project.
     *
     * @param req the search request containing project code, text and optional topK
     * @return the top matching chunks with text, score and metadata
     */
    @PostMapping(path = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Query knowledge chunks",
        description = "Performs a vector similarity search scoped to a project and returns the top matching chunks.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Results returned")
    })
    public List<KnowledgeChunkResponse> query(@Valid @RequestBody KnowledgeQueryRequest req) {
        int k = req.getTopK() != null ? req.getTopK() : 5;
        List<KnowledgeHit> hits = knowledgeQueryService.query(req.getProjectCode(), req.getQuery(), k);
        return mapper.toDtoList(hits);
    }

    /**
     * Triggers synchronization for a single project. Documents under the project's base path are chunked and upserted
     * into the vector store. When the project does not exist, 404 is returned.
     *
     * @param projectCode the unique code of the project to sync
     * @return counts of processed documents and chunks or 404 when project is missing
     */
    @PostMapping(path = "/sync/{projectCode}")
    @Operation(summary = "Sync single project",
        description = "Indexes documents for the given project into the vector store.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sync completed"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<KnowledgeSyncResponse> syncProject(
        @Parameter(description = "Project code to synchronize", required = true)
        @PathVariable String projectCode) {
        Optional<KnowledgeSyncService.SyncResult> res = knowledgeSyncService.syncProject(projectCode);
        return res
            .map(r -> ResponseEntity.ok(new KnowledgeSyncResponse(r.projectCode(), r.documents(), r.chunks())))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Triggers synchronization for all projects. Each project's documents are indexed into the vector store.
     *
     * @return a list of per-project sync results
     */
    @PostMapping(path = "/sync")
    @Operation(summary = "Sync all projects",
        description = "Indexes documents for all known projects into the vector store.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sync completed")
    })
    public List<KnowledgeSyncResponse> syncAll() {
        return knowledgeSyncService.syncAllProjects().stream()
            .map(r -> new KnowledgeSyncResponse(r.projectCode(), r.documents(), r.chunks()))
            .toList();
    }
}
