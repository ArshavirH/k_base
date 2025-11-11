package com.buildware.kbase.knowledge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith({MockitoExtension.class})
class KnowledgeQueryServiceTest {

    @Mock
    private VectorStore vectorStore;

    @InjectMocks
    private KnowledgeQueryService service;

    @Nested
    class Query {

        @Test
        void should_returnNearestChunks_when_queryByProject() {
            // GIVEN
            String projectCode = "testproj";
            String query = "any text";
            int topK = 2;
            Document d1 = new Document("alpha text", metadata(0));
            Document d2 = new Document("beta text", metadata(1));
            when(vectorStore.similaritySearch(org.mockito.ArgumentMatchers.any(SearchRequest.class)))
                .thenReturn(List.of(d1, d2));

            // WHEN
            List<KnowledgeHit> results = service.query(projectCode, query, topK);

            // THEN
            assertThat(results).hasSize(2);
            assertThat(results.get(0).text()).contains("alpha");
            assertThat(results.get(1).text()).contains("beta");
        }


        @Test
        void should_throw_when_blank_projectCode() {
            // GIVEN
            String projectCode = " ";
            String query = "q";

            // WHEN
            Throwable thrown = catchThrowable(() -> service.query(projectCode, query, 5));

            // THEN
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("projectCode must not be blank");
        }

        @Test
        void should_throw_when_blank_query() {
            // GIVEN
            String projectCode = "code";
            String query = "";

            // WHEN
            Throwable thrown = catchThrowable(() -> service.query(projectCode, query, 5));

            // THEN
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("text must not be blank");
        }
    }

    private static Map<String, Object> metadata(int idx) {
        Map<String, Object> md = new HashMap<>();
        md.put("docPath", "/doc.md");
        md.put("title", "Doc");
        md.put("chunkIndex", idx);
        md.put("projectCode", "testproj");
        return md;
    }
}
