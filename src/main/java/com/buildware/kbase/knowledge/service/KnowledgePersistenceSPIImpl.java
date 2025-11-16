package com.buildware.kbase.knowledge.service;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.knowledge.mapper.KnowledgeIngestionMapper;
import com.buildware.kbase.spi.KnowledgeIngestionSPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KnowledgePersistenceSPIImpl implements KnowledgeIngestionSPI {

    private final KnowledgePersistenceService maintenanceService;
    private final KnowledgeIngestionMapper mapper;

    @Override
    public KnowledgeIngestSummaryView ingest(KnowledgeIngestCommand command) {
        IngestDocument doc = mapper.toDomain(command);
        int count = maintenanceService.ingestDocument(doc);
        return mapper.toSummaryView(doc, count);
    }
}
