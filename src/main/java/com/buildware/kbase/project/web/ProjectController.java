package com.buildware.kbase.project.web;

import com.buildware.kbase.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project management endpoints. Exposes read-only listing and lookup for projects, and a sync endpoint that inspects
 * the knowledge directory and updates the repository accordingly.
 */
@RestController
@RequestMapping("/projects")
@Tag(name = "Projects", description = "Manage knowledge projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectApiMapper mapper;

    /**
     * List projects. Returns public projects by default. When {@code includeConfidential} is true, confidential
     * projects are included as well.
     *
     * @param includeConfidential whether confidential projects should be included
     * @return ordered list of project representations
     */
    @GetMapping
    @Operation(
        summary = "List projects",
        description = "Returns public projects by default; include confidential when requested"
    )
    public List<ProjectDTO> list(
        @Parameter(description = "Whether to include confidential projects")
        @RequestParam(name = "includeConfidential", defaultValue = "false")
        boolean includeConfidential) {
        return projectService.listProjects(includeConfidential).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get a project by its code.
     *
     * @param code the unique, URL-safe project code
     * @return 200 with the project when found or 404 otherwise
     */
    @GetMapping("/{code}")
    @Operation(summary = "Get project by code")
    @ApiResponse(responseCode = "200", description = "Project found",
        content = @Content(schema = @Schema(implementation = ProjectDTO.class)))
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<ProjectDTO> getByCode(
        @Parameter(description = "Unique project code")
        @PathVariable String code) {
        return projectService
            .getByCode(code)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}
