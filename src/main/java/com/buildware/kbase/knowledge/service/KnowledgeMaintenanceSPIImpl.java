package com.buildware.kbase.knowledge.service;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.spi.KnowledgeMaintenanceSPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KnowledgeMaintenanceSPIImpl implements KnowledgeMaintenanceSPI {

    private final KnowledgeMaintenanceService maintenanceService;

    @Override
    public IngestResponse ingest(IngestRequest request) {
        IngestDocument doc = new IngestDocument(
            request.projectCode(),
            request.content(),
            request.metadata(),
            request.tags()
        );
        int count = maintenanceService.ingestDocument(doc);
        return new IngestResponse(doc.projectCode(), count);
    }
}

