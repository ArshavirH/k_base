package com.buildware.kbase.mcp;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.buildware.kbase.ai.mcp.ProjectMcpTool;
import com.buildware.kbase.ai.mcp.ProjectMcpTool.DeleteProjectInput;
import com.buildware.kbase.ai.mcp.ProjectMcpTool.UpdateProjectInput;
import com.buildware.kbase.spi.ProjectInfoSPI;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectUpsert;
import java.util.Optional;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ProjectMcpToolTest {

    @Mock
    private ProjectInfoSPI projectInfoSPI;

    @InjectMocks
    private ProjectMcpTool tool;

    @Nested
    class Create {
        @Test
        void should_createProject_when_validInput() {
            // GIVEN
            ProjectUpsert upsert = random(ProjectUpsert.class);
            ProjectInfo expected = random(ProjectInfo.class);
            when(projectInfoSPI.create(upsert)).thenReturn(expected);

            // WHEN
            ProjectInfo actual = tool.create(upsert);

            // THEN
            assertThat(actual).isNotNull();
            assertThat(actual.id()).isEqualTo(expected.id());
            assertThat(actual.code()).isEqualTo(expected.code());
            assertThat(actual.basePath()).isEqualTo(expected.basePath());
        }
    }

    @Nested
    class Update {
        @Test
        void should_updateProject_when_exists() {
            // GIVEN
            String code = "proj-1";
            ProjectUpsert upsert = random(ProjectUpsert.class);
            UpdateProjectInput input = new UpdateProjectInput(code, upsert);
            ProjectInfo updated = random(ProjectInfo.class);
            when(projectInfoSPI.update(code, upsert)).thenReturn(Optional.of(updated));

            // WHEN
            ProjectInfo res = tool.update(input);

            // THEN
            assertThat(res).isNotNull();
            assertThat(res.code()).isEqualTo(updated.code());
        }

        @Test
        void should_throw_when_notFound() {
            // GIVEN
            String code = "missing";
            ProjectUpsert upsert = random(ProjectUpsert.class);
            UpdateProjectInput input = new UpdateProjectInput(code, upsert);
            when(projectInfoSPI.update(code, upsert)).thenReturn(Optional.empty());

            // WHEN
            Throwable thrown = catchThrowable(() -> tool.update(input));

            // THEN
            assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Project not found");
        }
    }

    @Nested
    class Delete {
        @Test
        void should_deleteProject_when_codeProvided() {
            // GIVEN
            String code = "to-delete";
            DeleteProjectInput input = new DeleteProjectInput(code);

            // WHEN
            String res = tool.delete(input);

            // THEN
            assertThat(res).isEqualTo("deleted:" + code);
            verify(projectInfoSPI, times(1)).deleteByCode(code);
        }
    }
}

