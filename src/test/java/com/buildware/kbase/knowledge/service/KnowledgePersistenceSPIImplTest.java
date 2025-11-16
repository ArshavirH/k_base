package com.buildware.kbase.knowledge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.spi.KnowledgeIngestionSPI.KnowledgeIngestCommand;
import com.buildware.kbase.spi.KnowledgeIngestionSPI.KnowledgeIngestSummaryView;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.buildware.kbase.knowledge.mapper.KnowledgeIngestionMapper;

@ExtendWith(MockitoExtension.class)
class KnowledgePersistenceSPIImplTest {

    @Mock
    private KnowledgePersistenceService maintenanceService;

    @Mock
    private KnowledgeIngestionMapper mapper;

    @InjectMocks
    private KnowledgePersistenceSPIImpl sut;

    @Test
    void should_ingest_when_valid_request() {
        // GIVEN
        KnowledgeIngestCommand request = new KnowledgeIngestCommand(
            "proj-1",
            "Some content to ingest",
            List.of("doc", "sample")
        );
        IngestDocument mappedDoc = new IngestDocument("proj-1", "Some content to ingest", List.of("doc", "sample"));
        when(mapper.toDomain(request)).thenReturn(mappedDoc);
        when(maintenanceService.ingestDocument(any(IngestDocument.class))).thenReturn(3);
        when(mapper.toSummaryView(mappedDoc, 3)).thenReturn(new KnowledgeIngestSummaryView("proj-1", 3));

        // WHEN
        KnowledgeIngestSummaryView response = sut.ingest(request);

        // THEN
        ArgumentCaptor<IngestDocument> captor = ArgumentCaptor.forClass(IngestDocument.class);
        verify(maintenanceService).ingestDocument(captor.capture());
        IngestDocument passed = captor.getValue();
        assertThat(passed.projectCode()).isEqualTo("proj-1");
        assertThat(passed.content()).isEqualTo("Some content to ingest");
        assertThat(passed.tags()).containsExactly("doc", "sample");

        assertThat(response.projectCode()).isEqualTo("proj-1");
        assertThat(response.ingestedChunks()).isEqualTo(3);
    }
}
