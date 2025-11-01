package com.buildware.kbase.project.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.domain.Visibility;
import com.buildware.kbase.project.service.ProjectService;
import com.buildware.kbase.project.web.dto.ProjectDTO;
import com.buildware.kbase.project.web.mapper.ProjectMapperImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(ProjectController.class)
@Import(ProjectMapperImpl.class)
@ExtendWith(InstancioExtension.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_listPublicProjects_when_noParamProvided() throws Exception {
        // GIVEN
        when(projectService.listProjects(false))
                .thenReturn(List.of(Project.builder()
                        .code("buildware")
                        .name("Buildware")
                        .basePath("/dev/buildware")
                        .visibility(Visibility.PUBLIC)
                        .build()));

        // WHEN
        MvcResult result = mockMvc.perform(get("/mcp/projects")).andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        List<ProjectDTO> body = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), new TypeReference<>() {});
        assertThat(body).hasSize(1);
        assertThat(body.get(0).code()).isEqualTo("buildware");
        assertThat(body.get(0).name()).isEqualTo("Buildware");
    }

    @Test
    void should_getProject_when_codeExists() throws Exception {
        // GIVEN
        when(projectService.getByCode("gift-boxes"))
                .thenReturn(Optional.of(Project.builder()
                        .code("gift-boxes")
                        .name("Gift Boxes")
                        .basePath("/dev/gift-boxes")
                        .visibility(Visibility.PUBLIC)
                        .build()));

        // WHEN
        MvcResult result = mockMvc.perform(get("/mcp/projects/gift-boxes")).andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        ProjectDTO body = objectMapper.readValue(result.getResponse().getContentAsByteArray(), ProjectDTO.class);
        assertThat(body.code()).isEqualTo("gift-boxes");
        assertThat(body.name()).isEqualTo("Gift Boxes");
    }

    @Test
    void should_returnSyncedProjects_when_syncIsCalled() throws Exception {
        // GIVEN
        when(projectService.syncFromFilesystem())
                .thenReturn(List.of(Project.builder()
                        .code("acme")
                        .name("Acme")
                        .basePath("/dev/acme")
                        .visibility(Visibility.PUBLIC)
                        .build()));

        // WHEN
        MvcResult result = mockMvc.perform(post("/mcp/projects/sync")).andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        List<ProjectDTO> body = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), new TypeReference<>() {});
        assertThat(body).hasSize(1);
        assertThat(body.get(0).code()).isEqualTo("acme");
    }
}
