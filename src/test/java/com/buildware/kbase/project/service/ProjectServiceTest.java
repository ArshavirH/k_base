package com.buildware.kbase.project.service;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.domain.Project.Visibility;
import com.buildware.kbase.project.repository.ProjectRepository;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService service;

    @Nested
    class Create {

        @Test
        void should_createProjectWithDefaultVisibility_when_validAndUnique() {
            // GIVEN
            var toCreate = Project.builder()
                .code("gift-boxes")
                .name("Gift Boxes")
                .basePath("/tmp/gift-boxes")
                .build();

            when(projectRepository.existsByCode("gift-boxes")).thenReturn(false);
            when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // WHEN
            Project saved = service.create(toCreate);

            // THEN
            assertThat(saved.getVisibility()).isEqualTo(Visibility.PUBLIC);
            verify(projectRepository, times(1)).save(any(Project.class));
        }

        @Test
        void should_throw_when_codeAlreadyExists() {
            // GIVEN
            var toCreate = Project.builder()
                .code("dup")
                .name("Duplicate")
                .basePath("/tmp/dup")
                .build();
            when(projectRepository.existsByCode("dup")).thenReturn(true);

            // WHEN
            Throwable thrown = catchThrowable(() -> service.create(toCreate));

            // THEN
            assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        }
    }

    @Nested
    class Update {

        @Test
        void should_updateAllProvidedFields_when_projectExists() {
            // GIVEN
            var existing = Project.builder()
                .code("acme")
                .name("Acme")
                .basePath("/data/acme")
                .visibility(Visibility.PUBLIC)
                .build();

            var updates = Project.builder()
                .name("Acme Inc")
                .basePath("/data/acme-new")
                .domainTags(List.of("java", "spring"))
                .description("desc")
                .visibility(Visibility.CONFIDENTIAL)
                .build();

            when(projectRepository.findByCode("acme")).thenReturn(Optional.of(existing));
            when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // WHEN
            Optional<Project> result = service.update("acme", updates);

            // THEN
            assertThat(result).isPresent();
            Project p = result.get();
            assertThat(p.getName()).isEqualTo("Acme Inc");
            assertThat(p.getBasePath()).isEqualTo("/data/acme-new");
            assertThat(p.getDomainTags()).containsExactlyInAnyOrder("java", "spring");
            assertThat(p.getDescription()).isEqualTo("desc");
            assertThat(p.getVisibility()).isEqualTo(Visibility.CONFIDENTIAL);
            verify(projectRepository, times(1)).save(any(Project.class));
        }

        @Test
        void should_returnEmpty_when_projectMissing() {
            // GIVEN
            when(projectRepository.findByCode("missing")).thenReturn(Optional.empty());

            // WHEN
            Optional<Project> res = service.update("missing", Project.builder().name("X").build());

            // THEN
            assertThat(res).isEmpty();
        }
    }

    @Nested
    class Delete {
        @Test
        void should_deleteAndReturnTrue_when_found() {
            // GIVEN
            var existing = random(Project.class).withCode("c1");
            when(projectRepository.findByCode("c1")).thenReturn(Optional.of(existing));

            // WHEN
            service.deleteByCode("c1");

            // THEN
            verify(projectRepository, times(1)).deleteById(existing.getId());
        }

        @Test
        void should_returnFalse_when_notFound() {
            // GIVEN
            when(projectRepository.findByCode("missing")).thenReturn(Optional.empty());

            // WHEN
            service.deleteByCode("missing");

        }
    }

    @Nested
    class Read {
        @Test
        void should_getByCode_when_exists() {
            // GIVEN
            var p = random(Project.class).withCode("p1");
            when(projectRepository.findByCode("p1")).thenReturn(Optional.of(p));

            // WHEN
            Optional<Project> res = service.getByCode("p1");

            // THEN
            assertThat(res).contains(p);
        }

        @Test
        void should_listPublic_when_includeConfidentialFalse() {
            // GIVEN
            var a = Project.builder().code("a").visibility(Visibility.PUBLIC).build();
            var b = Project.builder().code("b").visibility(Visibility.PUBLIC).build();
            when(projectRepository.findAllByVisibilityNot(Visibility.CONFIDENTIAL))
                .thenReturn(List.of(b, a));

            // WHEN
            List<Project> res = service.listProjects(false);

            // THEN
            assertThat(res).extracting(Project::getCode).containsExactly("a", "b");
        }
    }
}

