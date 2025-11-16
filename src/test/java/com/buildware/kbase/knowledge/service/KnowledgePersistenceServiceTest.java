package com.buildware.kbase.knowledge.service;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.knowledge.mapper.DocumentChunkMapper;
import com.buildware.kbase.spi.ProjectInfoSPI;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import java.util.List;
import java.util.Optional;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class KnowledgePersistenceServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ProjectInfoSPI projectInfoSPI;

    @Mock
    private DocumentChunkMapper documentChunkMapper;

    @InjectMocks
    private KnowledgePersistenceService service;

    @Test
    void should_ingestChunk_when_content() {
        // GIVEN
        var doc = random(IngestDocument.class);
        var docsToPersist = random(Document.class);
        var project = random(ProjectInfo.class);
        when(projectInfoSPI.getByCode(doc.projectCode()))
            .thenReturn(Optional.of(project));
        when(documentChunkMapper.toDocuments(doc)).thenReturn(List.of(docsToPersist));

        // WHEN
        int count = service.ingestDocument(doc);

        // THEN
        assertThat(count).isGreaterThanOrEqualTo(1);
        verify(vectorStore).add(List.of(docsToPersist));
    }
}
