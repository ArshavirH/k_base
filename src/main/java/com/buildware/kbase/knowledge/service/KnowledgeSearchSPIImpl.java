package com.buildware.kbase.knowledge.service;

import com.buildware.kbase.knowledge.mapper.KnowledgeHitMapper;
import com.buildware.kbase.spi.KnowledgeSearchSPI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter that bridges the knowledge text service to the SPI.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class KnowledgeSearchSPIImpl implements KnowledgeSearchSPI {

    private final KnowledgeQueryService service;
    private final KnowledgeHitMapper mapper;

    @Override
    public List<KnowledgeHitView> semanticSearch(KnowledgeQuery query) {
        return mapper.toViews(service.query(query.projectCode(), query.text(), query.topK(), query.tags()));
    }
}
