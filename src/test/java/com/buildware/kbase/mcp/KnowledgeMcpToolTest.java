package com.buildware.kbase.mcp;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.buildware.kbase.ai.mcp.KnowledgeMcpTool;
import com.buildware.kbase.spi.KnowledgeIngestionSPI;
import com.buildware.kbase.spi.KnowledgeIngestionSPI.KnowledgeIngestCommand;
import com.buildware.kbase.spi.KnowledgeIngestionSPI.KnowledgeIngestSummaryView;
import com.buildware.kbase.spi.KnowledgeSearchSPI;
import com.buildware.kbase.spi.KnowledgeSearchSPI.KnowledgeHitView;
import com.buildware.kbase.spi.KnowledgeSearchSPI.KnowledgeQuery;
import java.util.List;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class KnowledgeMcpToolTest {

    @Mock
    private KnowledgeSearchSPI queryPort;

    @Mock
    private KnowledgeIngestionSPI maintenancePort;

    @InjectMocks
    private KnowledgeMcpTool tool;

    @Test
    void should_returnHits_when_validInput() {
        // GIVEN
        KnowledgeHitView expected = random(KnowledgeHitView.class);
        KnowledgeQuery in = random(KnowledgeQuery.class);
        when(queryPort.semanticSearch(in)).thenReturn(List.of(expected));

        // WHEN
        List<KnowledgeHitView> actual = tool.semanticSearch(in);

        // THEN
        assertThat(actual).hasSize(1);
        KnowledgeHitView first = actual.stream().findFirst().orElseThrow();
        assertThat(first.text()).isEqualTo(expected.text());
        assertThat(first.score()).isEqualTo(expected.score());
        assertThat(first.title()).isEqualTo(expected.title());
        assertThat(first.docPath()).isEqualTo(expected.docPath());
        assertThat(first.chunkIndex()).isEqualTo(expected.chunkIndex());
    }

    @Test
    void should_ingestDocument_when_validRequest() {
        // GIVEN
        KnowledgeIngestCommand req = random(KnowledgeIngestCommand.class);
        KnowledgeIngestSummaryView expected = random(KnowledgeIngestSummaryView.class);
        when(maintenancePort.ingest(req)).thenReturn(expected);

        // WHEN
        KnowledgeIngestSummaryView actual = tool.ingest(req);

        // THEN
        assertThat(actual.projectCode()).isEqualTo(expected.projectCode());
        assertThat(actual.ingestedChunks()).isEqualTo(expected.ingestedChunks());
    }
}
