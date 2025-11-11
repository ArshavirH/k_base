package com.buildware.kbase.knowledge.mapper;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

class DocumentChunkMapperTest {

    private final DocumentChunkMapper mapper = new DocumentChunkMapper();

    @Nested
    class SplitAndBuild {

        @Test
        void should_buildDocumentsAndMarker_withMetadata() {
            // GIVEN
            IngestDocument doc = random(IngestDocument.class);

            // WHEN
            List<Document> docs = mapper.toDocuments(doc);

            // THEN
            assertThat(docs.size()).isGreaterThanOrEqualTo(2);

            Document document = docs.getLast();
            assertThat(document.getMetadata().keySet()).containsAll(document.getMetadata().keySet());
        }

        @Test
        void should_includeMarker_evenWhen_textShort() {
            // GIVEN
            IngestDocument doc = random(IngestDocument.class);

            // WHEN
            List<Document> docs = mapper.toDocuments(doc);

            // THEN
            assertThat(docs.size()).isGreaterThanOrEqualTo(1);
            Document marker = docs.getLast();
            assertThat(marker.getMetadata().get("type")).isEqualTo("marker");
        }
    }
}
