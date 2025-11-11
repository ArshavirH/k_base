package com.buildware.kbase.knowledge.service;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.spi.ProjectInfoSPI;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import java.util.Optional;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class KnowledgeMaintenanceServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ProjectInfoSPI projectInfoSPI;

    @InjectMocks
    private KnowledgeMaintenanceService service;

    @Test
    void should_ingestChunk_when_content() {
        // GIVEN
        var doc = random(IngestDocument.class);
        Mockito.when(projectInfoSPI.getByCode(Mockito.anyString()))
            .thenReturn(Optional.of(random(ProjectInfo.class)));

        // WHEN
        int count = service.ingestDocument(doc);

        // THEN
        assertThat(count).isGreaterThanOrEqualTo(1);
        verify(vectorStore).add(anyList());
    }
}
