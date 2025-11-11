package com.buildware.kbase.project.web;

import static com.buildware.kbase.toolkit.instancio.InstancioUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.service.ProjectService;
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
@Import(ProjectApiMapperImpl.class)
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
        var project = random(Project.class);
        when(projectService.listProjects(false))
            .thenReturn(List.of(project));

        // WHEN
        MvcResult result = mockMvc.perform(get("/projects")).andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        List<ProjectDTO> body =
            objectMapper.readValue(result.getResponse().getContentAsByteArray(), new TypeReference<>() {
            });
        var actual = body.getFirst();
        assertThat(body).hasSize(1);
        assertThat(actual.code()).isEqualTo(project.getCode());
        assertThat(actual.name()).isEqualTo(project.getName());
    }

    @Test
    void should_getProject_when_codeExists() throws Exception {
        // GIVEN
        var project = random(Project.class);
        when(projectService.getByCode("gift-boxes")).thenReturn(Optional.of(project));

        // WHEN
        MvcResult result = mockMvc.perform(get("/projects/gift-boxes")).andReturn();

        // THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        ProjectDTO body = objectMapper.readValue(result.getResponse().getContentAsByteArray(), ProjectDTO.class);
        assertThat(body.code()).isEqualTo(project.getCode());
        assertThat(body.name()).isEqualTo(project.getName());
    }

    // Filesystem sync endpoint removed; corresponding test deleted.

    @Test
    void should_createProject_when_validRequest() throws Exception {
        // GIVEN
        var project = random(Project.class);
        when(projectService.create(org.mockito.ArgumentMatchers.any(Project.class))).thenReturn(project);

        // WHEN
        MvcResult res = mockMvc.perform(post("/projects")
                .contentType("application/json")
                .content("{\"code\":\"p1\",\"name\":\"P1\",\"basePath\":\"/tmp\"}"))
            .andReturn();

        // THEN
        assertThat(res.getResponse().getStatus()).isEqualTo(201);
    }

    @Test
    void should_updateProject_when_exists() throws Exception {
        // GIVEN
        var project = random(Project.class);
        when(projectService.update(org.mockito.Mockito.eq("p1"), org.mockito.ArgumentMatchers.any(Project.class)))
            .thenReturn(java.util.Optional.of(project));

        // WHEN
        MvcResult res = mockMvc.perform(put("/projects/p1")
                .contentType("application/json")
                .content("{\"name\":\"New\",\"basePath\":\"/tmp\"}"))
            .andReturn();

        // THEN
        assertThat(res.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    void should_deleteProject_when_exists() throws Exception {
        // GIVEN
        doNothing().when(projectService).deleteByCode("p1");

        // WHEN
        MvcResult res = mockMvc.perform(delete("/projects/p1")).andReturn();

        // THEN
        assertThat(res.getResponse().getStatus()).isEqualTo(204);
    }
}
