package com.buildware.kbase.project.repository;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.toolkit.AbstractDataJpaTest;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(InstancioExtension.class)
class ProjectRepositoryTest extends AbstractDataJpaTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void should_saveAndFindProject_when_codeMatches() {
        // GIVEN
        Project p = random(Project.class).withId(null);

        // WHEN
        projectRepository.save(p);

        // THEN
        assertThat(projectRepository.existsByCode(p.getCode())).isTrue();
        assertThat(projectRepository.findByCode(p.getCode())).isPresent();
    }
}
