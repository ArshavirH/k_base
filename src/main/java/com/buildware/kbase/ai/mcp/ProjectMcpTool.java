package com.buildware.kbase.ai.mcp;

import com.buildware.kbase.spi.ProjectInfoSPI;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectUpsert;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectMcpTool {

    private final ProjectInfoSPI projectInfoSPI;

    @Tool(name = "project.create", description = "Create a project with given properties")
    public ProjectInfo create(@Valid ProjectUpsert input) {
        return projectInfoSPI.create(input);
    }

    @Tool(name = "project.update", description = "Update a project by code with given properties")
    public ProjectInfo update(@Valid UpdateProjectInput input) {
        return projectInfoSPI.update(input.code(), input.upsert())
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + input.code()));
    }

    @Tool(name = "project.delete", description = "Delete a project by its code")
    public String delete(@Valid DeleteProjectInput input) {
        projectInfoSPI.deleteByCode(input.code());
        return "deleted:" + input.code();
    }

    public record UpdateProjectInput(String code, ProjectUpsert upsert) {}

    public record DeleteProjectInput(String code) {}
}

