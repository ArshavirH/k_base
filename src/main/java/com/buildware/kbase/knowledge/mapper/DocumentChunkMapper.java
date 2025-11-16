package com.buildware.kbase.knowledge.mapper;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentChunkMapper {

    public static final String MD_CHUNK_INDEX = "chunkIndex";
    public static final String MD_TOTAL_CHUNKS = "totalChunks";
    public static final String MD_PROJECT_CODE = "projectCode";
    public static final String MD_CONTENT_HASH = "contentHash";
    public static final String MD_TAGS = "tags";
    public static final String MD_TYPE = "type";
    public static final String MD_MARKER_TYPE = "marker";

    public List<Document> toDocuments(IngestDocument doc) {
        List<Document> docs = DocumentChunker.toDocuments(doc);

        int total = docs.size();
        for (int i = 0; i < total; i++) {
            docs.get(i).getMetadata().put(MD_CHUNK_INDEX, i);
            docs.get(i).getMetadata().put(MD_TOTAL_CHUNKS, total);
        }
        return docs;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DocumentChunker {

        private static final String MARKER_PREFIX = "__KBASE_MARKER__:";

        public static List<Document> toDocuments(IngestDocument doc) {

            var contentHash = sha256Hex(doc.content());

            Document source = new Document(doc.content());
            source.getMetadata().put(MD_PROJECT_CODE, doc.projectCode());
            source.getMetadata().put(MD_CONTENT_HASH, contentHash);
            if (doc.tags() != null && !doc.tags().isEmpty()) {
                source.getMetadata().put(MD_TAGS, doc.tags());
            }

            List<Document> splitDocs = new TokenTextSplitter().apply(List.of(source));

            List<Document> toStore = new ArrayList<>(splitDocs.size() + 1);
            for (int index = 0; index < splitDocs.size(); index++) {
                Document d = splitDocs.get(index);
                d.getMetadata().put(MD_CHUNK_INDEX, index);
                toStore.add(d);
            }

            String tag = doc.projectCode() + "|" + contentHash;
            String markerText = MARKER_PREFIX + tag;
            Document marker = new Document(markerText);
            marker.getMetadata().put(MD_TYPE, DocumentChunkMapper.MD_MARKER_TYPE);
            marker.getMetadata().put(MD_PROJECT_CODE, doc.projectCode());
            marker.getMetadata().put(MD_CONTENT_HASH, contentHash);
            toStore.add(marker);

            return toStore;
        }

        private static String sha256Hex(String text) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder(digest.length * 2);
                for (byte b : digest) {
                    sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                    sb.append(Character.forDigit((b & 0xF), 16));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 not available", e);
            }
        }
    }
}
