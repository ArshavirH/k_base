package com.buildware.kbase.knowledge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.spi.KnowledgeMaintenanceSPI.IngestRequest;
import com.buildware.kbase.spi.KnowledgeMaintenanceSPI.IngestResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KnowledgeMaintenanceSPIImplTest {

    @Mock
    private KnowledgeMaintenanceService maintenanceService;

    @InjectMocks
    private KnowledgeMaintenanceSPIImpl sut;

    @Test
    void should_ingest_when_valid_request() {
        // GIVEN
        IngestRequest request = new IngestRequest(
            "proj-1",
            "Some content to ingest",
            Map.of("source", "test"),
            List.of("doc", "sample")
        );
        when(maintenanceService.ingestDocument(any(IngestDocument.class))).thenReturn(3);

        // WHEN
        IngestResponse response = sut.ingest(request);

        // THEN
        ArgumentCaptor<IngestDocument> captor = ArgumentCaptor.forClass(IngestDocument.class);
        verify(maintenanceService).ingestDocument(captor.capture());
        IngestDocument passed = captor.getValue();
        assertThat(passed.projectCode()).isEqualTo("proj-1");
        assertThat(passed.content()).isEqualTo("Some content to ingest");
        assertThat(passed.metadata()).containsEntry("source", "test");
        assertThat(passed.tags()).containsExactly("doc", "sample");

        assertThat(response.projectCode()).isEqualTo("proj-1");
        assertThat(response.ingestedChunks()).isEqualTo(3);
    }
}

