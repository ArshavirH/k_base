package com.buildware.kbase.project.service;

import com.buildware.kbase.config.KnowledgeProperties;
import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.domain.Visibility;
import com.buildware.kbase.project.repository.ProjectRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final KnowledgeProperties knowledgeProperties;

    public List<Project> listProjects(boolean includeConfidential) {
        List<Project> all = includeConfidential
            ? projectRepository.findAll()
            : projectRepository.findAllByVisibilityNot(Visibility.CONFIDENTIAL);
        return all.stream()
            .sorted(Comparator.comparing(Project::getCode))
            .collect(Collectors.toList());
    }

    public Optional<Project> getByCode(String code) {
        return projectRepository.findByCode(code);
    }

    @Transactional
    public List<Project> syncFromFilesystem() {
        String docsPath = knowledgeProperties.docsPath();
        Path base = Path.of(docsPath);
        log.info("Starting project sync from base path: {}", base.toAbsolutePath());

        if (!Files.exists(base) || !Files.isDirectory(base)) {
            log.error(
                "Knowledge base path does not exist or not a directory: {}",
                base.toAbsolutePath());
            return List.of();
        }

        List<Project> discovered = new ArrayList<>();
        try {
            try (var stream = Files.list(base)) {
                stream.filter(Files::isDirectory)
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .forEach(dir -> discovered.add(buildProjectFromDir(dir)));
            }
        } catch (IOException e) {
            log.error("Failed to list knowledge directories at {}", base.toAbsolutePath(), e);
            return List.of();
        }

        Instant now = Instant.now();
        List<Project> toSave = new ArrayList<>();
        for (Project p : discovered) {
            projectRepository
                .findByCode(p.getCode())
                .ifPresentOrElse(
                    existing -> {
                        existing.setName(p.getName());
                        existing.setBasePath(p.getBasePath());
                        existing.setLastSyncAt(now);
                        toSave.add(existing);
                    },
                    () -> {
                        p.setLastSyncAt(now);
                        toSave.add(p);
                    });
        }

        if (toSave.isEmpty()) {
            log.info("No projects to sync. Repository is up to date.");
            return listProjects(true);
        }

        List<Project> saved = projectRepository.saveAll(toSave);
        log.info("Synchronized {} projects from filesystem.", saved.size());
        return saved;
    }

    private static Project buildProjectFromDir(Path dir) {
        String code = slug(dir.getFileName().toString());
        String name = humanize(dir.getFileName().toString());
        return Project.builder()
            .code(code)
            .name(name)
            .basePath(dir.toAbsolutePath().toString())
            .visibility(Visibility.PUBLIC)
            .build();
    }

    private static String slug(String input) {
        String cleaned = input.replaceAll("[^a-zA-Z0-9\\-_]", "-");
        cleaned = cleaned.replaceAll("-+", "-");
        return cleaned.toLowerCase(Locale.ROOT);
    }

    private static String humanize(String input) {
        String replaced = input.replace('_', ' ').replace('-', ' ');
        String[] parts = replaced.split("\\s+");
        return java.util.Arrays.stream(parts)
            .filter(s -> !s.isBlank())
            .map(s -> s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1))
            .collect(Collectors.joining(" "));
    }
}
