package com.buildware.kbase.knowledge.service;

import static com.buildware.kbase.knowledge.mapper.DocumentChunkMapper.MD_PROJECT_CODE;
import static com.buildware.kbase.knowledge.mapper.DocumentChunkMapper.MD_TAGS;

import com.buildware.kbase.knowledge.domain.KnowledgeHit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
@lombok.RequiredArgsConstructor
public class KnowledgeQueryService {

    private static final double DEFAULT_SIMILARITY_SCORE = 0.30;

    private final VectorStore vectorStore;

    public List<KnowledgeHit> query(String projectCode, String query, int topK, List<String> tags) {
        Validate.notBlank(projectCode, "projectCode must not be blank");
        var searchQuery = SearchRequest.builder()
            .query(query)
            .topK(topK > 0 ? Math.min(topK, 50) : 5)
            .similarityThreshold(DEFAULT_SIMILARITY_SCORE)
            .filterExpression(buildFilterExpression(projectCode, tags))
            .build();
        List<Document> docs = vectorStore.similaritySearch(searchQuery);
        return docs.stream().map(KnowledgeQueryService::toHit).collect(Collectors.toList());
    }

    private static String buildFilterExpression(String projectCode, List<String> tags) {
        String base = "%s == '%s'".formatted(MD_PROJECT_CODE, escape(projectCode));

        if (tags == null || tags.isEmpty()) {
            return base;
        }
        String values = tags.stream()
            .filter(s -> s != null && !s.isBlank())
            .map(KnowledgeQueryService::quote)
            .collect(Collectors.joining(", "));
        if (values.isBlank()) {
            return base;
        }
        return base + " && " + MD_TAGS + " IN [" + values + "]";
    }

    private static String quote(String s) {
        return "'" + escape(s) + "'";
    }

    private static String escape(String s) {
        return s.replace("'", "''");
    }

    private static KnowledgeHit toHit(Document doc) {
        String text = doc.getFormattedContent();
        Map<String, Object> md = doc.getMetadata();
        String docPath = md.get("docPath") != null ? String.valueOf(md.get("docPath")) : null;
        String title = md.get("title") != null ? String.valueOf(md.get("title")) : null;
        int chunkIndex = 0;
        Object idx = md.get("chunkIndex");
        if (idx instanceof Number n) {
            chunkIndex = n.intValue();
        }

        double scoreVal = 0.0;
        try {
            var scoreMethod = doc.getClass().getMethod("getScore");
            Object score = scoreMethod.invoke(doc);
            if (score instanceof Number n) {
                scoreVal = n.doubleValue();
            }
        } catch (Exception ignore) {
            // leave default score
        }
        return new KnowledgeHit(text, scoreVal, docPath, title, chunkIndex);
    }
}
