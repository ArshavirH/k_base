package com.buildware.kbase.knowledge.web;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import com.buildware.kbase.knowledge.service.KnowledgeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
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
    private final KnowledgeApiMapper mapper;

    public KnowledgeController(
        KnowledgeQueryService knowledgeQueryService,
        KnowledgeApiMapper mapper
    ) {
        this.knowledgeQueryService = knowledgeQueryService;
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
}
