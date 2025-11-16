package com.buildware.kbase.knowledge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class KnowledgeQueryServiceTest {

    @Mock
    private VectorStore vectorStore;

    @InjectMocks
    private KnowledgeQueryService service;

    @Nested
    class FilterExpression {

        @Test
        void should_useProjectOnly_when_tagsNull() {
            // GIVEN
            String project = "proj";
            String query = "hello";

            // WHEN
            service.query(project, query, 5, null);

            // THEN
            SearchRequest captured = captureSearchRequest();
            String filter = extractFilterExpression(captured);
            assertThat(filter).contains("projectCode");
            assertThat(filter).contains("proj");
        }

        @Test
        void should_useProjectOnly_when_tagsEmptyOrBlank() {
            // GIVEN
            String project = "proj";
            String query = "hello";

            // WHEN
            service.query(project, query, 5, Collections.emptyList());
            service.query(project, query, 5, List.of("", "  "));

            // THEN
            ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
            verify(vectorStore, org.mockito.Mockito.times(2)).similaritySearch(captor.capture());
            List<SearchRequest> calls = captor.getAllValues();
            for (SearchRequest req : calls) {
                String filter = extractFilterExpression(req);
                assertThat(filter).contains("projectCode");
                assertThat(filter).contains(project);
            }
        }

        @Test
        void should_appendIN_when_tagsProvided() {
            // GIVEN
            String project = "proj";
            String query = "hello";
            List<String> tags = List.of("core", "api");

            // WHEN
            service.query(project, query, 5, tags);

            // THEN
            SearchRequest captured = captureSearchRequest();
            String filter = extractFilterExpression(captured);
            assertThat(filter).contains("projectCode");
            assertThat(filter).contains(project);
            assertThat(filter).contains("tags");
            assertThat(filter).contains("core", "api");
        }

        @Test
        void should_escapeQuotes_in_projectAndTags() {
            // GIVEN
            String project = "pro'j"; // contains quote
            String query = "hello";
            List<String> tags = List.of("a'b", "c");

            // WHEN
            service.query(project, query, 5, tags);

            // THEN
            SearchRequest captured = captureSearchRequest();
            String filter = extractFilterExpression(captured);
            assertThat(filter)
                .contains(
                    "left=Expression[type=EQ, left=Key[key=projectCode]",
                    "right=Value[value=pro''j]",
                    "right=Expression[type=IN, left=Key[key=tags], right=Value[value=[a''b, c]]"
                );
        }
    }

    private SearchRequest captureSearchRequest() {
        ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore).similaritySearch(captor.capture());
        return captor.getValue();
    }

    private String extractFilterExpression(SearchRequest req) {
        try {
            Method m = req.getClass().getMethod("getFilterExpression");
            Object val = m.invoke(req);
            return val != null ? String.valueOf(val) : null;
        } catch (Exception ignore) {
            // fall back to field access
            try {
                Field f = req.getClass().getDeclaredField("filterExpression");
                f.setAccessible(true);
                Object val = f.get(req);
                return val != null ? String.valueOf(val) : null;
            } catch (Exception e) {
                throw new AssertionError("Unable to read filterExpression from SearchRequest", e);
            }
        }
    }
}

