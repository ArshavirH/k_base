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
public class KnowledgeQueryService {

    private final VectorStore vectorStore;

    public KnowledgeQueryService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

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
        KnowledgeHit hit = new KnowledgeHit();
        hit.setText(doc.getFormattedContent());
        Map<String, Object> md = doc.getMetadata();
        Object path = md.get("docPath");
        if (path != null) {
            hit.setDocPath(String.valueOf(path));
        }
        Object title = md.get("title");
        if (title != null) {
            hit.setTitle(String.valueOf(title));
        }
        Object idx = md.get("chunkIndex");
        if (idx instanceof Number n) {
            hit.setChunkIndex(n.intValue());
        }

        try {
            var scoreMethod = doc.getClass().getMethod("getScore");
            Object score = scoreMethod.invoke(doc);
            if (score instanceof Number n) {
                hit.setScore(n.doubleValue());
            }
        } catch (Exception ignore) {
            hit.setScore(0.0);
        }
        return hit;
    }
}
