package com.buildware.kbase.knowledge.service;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import com.buildware.kbase.knowledge.mapper.DocumentChunkMapper;
import com.buildware.kbase.spi.ProjectInfoSPI;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
@lombok.RequiredArgsConstructor
public class KnowledgeMaintenanceService {

    private final VectorStore vectorStore;
    private final DocumentChunkMapper documentChunkMapper;
    private final ProjectInfoSPI projectInfoSPI;

    /**
     * Ingest a long document by splitting into chunks and persisting to the vector store.
     *
     * @param doc IngestDocument containing metadata and content
     */
    public int ingestDocument(IngestDocument doc) {
        if (doc == null || StringUtils.isBlank(doc.projectCode())) {
            throw new IllegalArgumentException("projectCode must not be blank");
        }
        if (StringUtils.isBlank(doc.content())) {
            throw new IllegalArgumentException("content must not be blank");
        }
        projectInfoSPI.getByCode(doc.projectCode())
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + doc.projectCode()));

        DocumentChunkMapper mapper =
            this.documentChunkMapper != null ? this.documentChunkMapper : new DocumentChunkMapper();
        List<Document> docs = mapper.toDocuments(doc);
        vectorStore.add(docs);
        return docs.size();
    }
}
