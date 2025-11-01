package com.buildware.kbase.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.buildware.kbase.config.KnowledgeProperties;
import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.repository.ProjectRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ProjectServiceTest {

    @Mock
    private ProjectRepository repo;

    @Mock
    private KnowledgeProperties knowledgeProperties;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void should_syncProjects_when_directoriesExist() throws IOException {
        // GIVEN
        Path temp = Files.createTempDirectory("kb-test");
        Files.createDirectories(temp.resolve("buildware"));
        Files.createDirectories(temp.resolve("gift-boxes"));

        when(repo.findByCode(any())).thenReturn(Optional.empty());
        when(repo.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(knowledgeProperties.docsPath()).thenReturn(temp.toString());

        // WHEN
        List<Project> saved = projectService.syncFromFilesystem();

        // THEN
        assertThat(saved).hasSize(2);
        assertThat(saved)
            .extracting(Project::getCode)
            .containsExactlyInAnyOrder("buildware", "gift-boxes");
    }
}
