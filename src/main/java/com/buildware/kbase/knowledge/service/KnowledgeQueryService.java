package com.buildware.kbase.knowledge.service;

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

    private final VectorStore vectorStore;

    public List<KnowledgeHit> query(String projectCode, String query, int topK) {
        Validate.notBlank(projectCode, "projectCode must not be blank");
        Validate.notBlank(query, "text must not be blank");
        var searchQuery = SearchRequest.builder()
            .query(query)
            .topK(topK > 0 ? Math.min(topK, 50) : 5)
            .filterExpression("projectCode == '%s'".formatted(projectCode))
            .build();
        List<Document> docs = vectorStore.similaritySearch(searchQuery);
        return docs.stream().map(KnowledgeQueryService::toHit).collect(Collectors.toList());
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
