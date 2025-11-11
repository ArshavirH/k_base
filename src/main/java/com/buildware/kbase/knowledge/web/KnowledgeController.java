package com.buildware.kbase.knowledge.web;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import com.buildware.kbase.knowledge.service.KnowledgeQueryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private final KnowledgeApiMapper mapper;

    /**
     * Query knowledge chunks using vector similarity.
     *
     * @param req request body with project code, free-text query and optional topK (default 5)
     * @return list of top matching chunks with text, score, title and document metadata
     */
    @PostMapping(path = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<KnowledgeChunkDTO> query(@Valid @RequestBody KnowledgeQueryDTO req) {
        int k = req.topK() != null ? req.topK() : 5;
        List<KnowledgeHit> hits = knowledgeQueryService.query(req.projectCode(), req.query(), k);
        return mapper.toDtoList(hits);
    }
}
