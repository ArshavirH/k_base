package com.buildware.kbase.project.service;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.mapper.ProjectMapper;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import java.util.List;
import java.util.Optional;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ProjectInfoSPIImplTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectInfoSPIImpl spi;

    @Nested
    class GetByCode {

        @Test
        void should_returnProjectInfo_when_codeExists() {
            // GIVEN
            Project project = random(Project.class).withCode("acme");
            ProjectInfo info = new ProjectInfo(randomUUID(), project.getCode(), project.getBasePath());
            when(projectService.getByCode("acme")).thenReturn(Optional.of(project));
            when(projectMapper.toView(project)).thenReturn(info);

            // WHEN
            Optional<ProjectInfo> result = spi.getByCode("acme");

            // THEN
            assertThat(result).contains(info);
        }

        @Test
        void should_returnEmpty_when_codeMissing() {
            // GIVEN
            when(projectService.getByCode("missing")).thenReturn(Optional.empty());

            // WHEN
            Optional<ProjectInfo> result = spi.getByCode("missing");

            // THEN
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class ListAll {
        @Test
        void should_listAllMapped_when_projectsAvailable() {
            // GIVEN
            Project a = Project.builder().code("a").basePath("/a").build();
            Project b = Project.builder().code("b").basePath("/b").build();
            when(projectService.listProjects(false)).thenReturn(List.of(b, a));

            ProjectInfo ia = new ProjectInfo(randomUUID(), "a", "/a");
            ProjectInfo ib = new ProjectInfo(randomUUID(), "b", "/b");
            when(projectMapper.toView(a)).thenReturn(ia);
            when(projectMapper.toView(b)).thenReturn(ib);

            // WHEN
            List<ProjectInfo> res = spi.listAll();

            // THEN
            assertThat(res).containsExactly(ib, ia);
        }
    }
}

