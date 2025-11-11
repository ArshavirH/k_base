package com.buildware.kbase.knowledge.mapper;

import com.buildware.kbase.knowledge.domain.IngestDocument;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

@Component
public class DocumentChunkMapper {


    public List<Document> toDocuments(IngestDocument doc) {
        List<Document> docs = DocumentChunker.toDocuments(doc);

        int total = docs.size();
        for (int i = 0; i < total; i++) {
            docs.get(i).getMetadata().put("chunkIndex", i);
            docs.get(i).getMetadata().put("totalChunks", total);
        }
        return docs;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DocumentChunker {

        private static final String MARKER_PREFIX = "__KBASE_MARKER__:";

        public static List<Document> toDocuments(IngestDocument doc) {

            var contentHash = sha256Hex(doc.content());

            Document source = new Document(doc.content());
            source.getMetadata().put("projectCode", doc.projectCode());
            source.getMetadata().put("contentHash", contentHash);
            if (doc.metadata() != null && !doc.metadata().isEmpty()) {
                for (Map.Entry<String, String> e : doc.metadata().entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        source.getMetadata().put(e.getKey(), e.getValue());
                    }
                }
            }
            if (doc.tags() != null && !doc.tags().isEmpty()) {
                source.getMetadata().put("tags", doc.tags());
            }

            List<Document> splitDocs = new TokenTextSplitter().apply(List.of(source));

            List<Document> toStore = new ArrayList<>(splitDocs.size() + 1);
            for (int index = 0; index < splitDocs.size(); index++) {
                Document d = splitDocs.get(index);
                d.getMetadata().put("chunkIndex", index);
                toStore.add(d);
            }

            String tag = doc.projectCode() + "|" + contentHash;
            String markerText = MARKER_PREFIX + tag;
            Document marker = new Document(markerText);
            marker.getMetadata().put("type", "marker");
            marker.getMetadata().put("projectCode", doc.projectCode());
            marker.getMetadata().put("contentHash", contentHash);
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
