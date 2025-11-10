package com.buildware.kbase.knowledge.service;

import com.buildware.kbase.spi.ProjectInfoSPI;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeSyncService {

    private static final String MARKER_PREFIX = "__KBASE_MARKER__:";

    private final ProjectInfoSPI projectInfoSPI;
    private final VectorStore vectorStore;

    public record SyncResult(String projectCode, int documents, int chunks) {

    }

    @Transactional
    public Optional<SyncResult> syncProject(String projectCode) {
        Optional<ProjectInfo> projectOpt = projectInfoSPI.getByCode(projectCode);
        if (projectOpt.isEmpty()) {
            log.warn("Project '{}' not found for knowledge sync", projectCode);
            return Optional.empty();
        }

        ProjectInfo project = projectOpt.get();
        Path base = Path.of(project.basePath());
        if (!Files.exists(base) || !Files.isDirectory(base)) {
            log.warn("Base path for project '{}' does not exist: {}", projectCode, base);
            return Optional.of(new SyncResult(projectCode, 0, 0));
        }

        List<Path> files = listDocs(base);
        int docs = 0;
        int chunks = 0;
        for (Path p : files) {
            try {
                chunks += upsertDocumentAndChunks(project, base, p);
                docs++;
            } catch (Exception e) {
                log.error("Failed to sync document {} for project {}", p, projectCode, e);
            }
        }
        return Optional.of(new SyncResult(projectCode, docs, chunks));
    }

    @Transactional
    public List<SyncResult> syncAllProjects() {
        return projectInfoSPI.listAll().stream()
            .map(ProjectInfo::code)
            .map(this::syncProject)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private int upsertDocumentAndChunks(ProjectInfo project, Path base, Path file) throws IOException {
        String relPath = base.relativize(file).toString();
        String content = Files.readString(file, StandardCharsets.UTF_8);
        String contentHash = sha256Hex(content);

        String tag = project.code() + "|" + relPath + "|" + contentHash;
        String markerText = MARKER_PREFIX + tag;

        if (isTagAlreadyLoaded(markerText, project.code())) {
            log.info("[{}] already loaded. Skipping.", tag);
            return 0;
        }

        Document source = new Document(content);
        source.getMetadata().put("projectCode", project.code());
        source.getMetadata().put("docPath", relPath);
        source.getMetadata().put("title", inferTitle(file));
        source.getMetadata().put("contentHash", contentHash);

        List<Document> splitDocs = new TokenTextSplitter().apply(List.of(source));

        List<Document> toStore = new ArrayList<>(splitDocs.size() + 1);
        for (int index = 0; index < splitDocs.size(); index++) {
            Document d = splitDocs.get(index);
            d.getMetadata().put("chunkIndex", index);
            toStore.add(d);
        }

        Document marker = new Document(markerText);
        marker.getMetadata().put("type", "marker");
        marker.getMetadata().put("projectCode", project.code());
        marker.getMetadata().put("docPath", relPath);
        marker.getMetadata().put("contentHash", contentHash);
        toStore.add(marker);

        vectorStore.add(toStore);
        return splitDocs.size();
    }

    private static List<Path> listDocs(Path base) {
        List<Path> result = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(base)) {
            stream.filter(Files::isRegularFile)
                .filter(p -> {
                    String name = p.getFileName().toString().toLowerCase();
                    return name.endsWith(".md") || name.endsWith(".markdown") || name.endsWith(".txt");
                })
                .forEach(result::add);
        } catch (IOException e) {
            log.error("Failed to walk knowledge directory {}", base, e);
        }
        return result;
    }

    private static String inferTitle(Path file) {
        String filename = file.getFileName().toString();
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (String line : lines) {
                String l = line.trim();
                if (l.startsWith("#")) {
                    l = l.replaceFirst("^#+\\s*", "").trim();
                    if (!l.isBlank()) {
                        return l;
                    }
                }
            }
        } catch (IOException ignored) {
            // fall back to filename
        }
        return stripExtension(humanize(filename));
    }

    private static String stripExtension(String name) {
        int i = name.lastIndexOf('.');
        return i > 0 ? name.substring(0, i) : name;
    }

    private static String humanize(String input) {
        String replaced = input.replace('_', ' ').replace('-', ' ');
        return Stream.of(replaced.split("\\s+"))
            .filter(s -> !s.isBlank())
            .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
            .collect(Collectors.joining(" "));
    }

    private boolean isTagAlreadyLoaded(String markerText, String projectCode) {
        try {
            List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
                .query(markerText)
                .topK(1)
                .filterExpression("type == 'marker' && projectCode == '%s'".formatted(projectCode))
                .build());
            return results.stream().anyMatch(doc -> markerText.equalsIgnoreCase(doc.getFormattedContent()));
        } catch (Exception e) {
            log.error("Error checking if tag already loaded [{}]: {}", markerText, e.getMessage());
            return false;
        }
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
