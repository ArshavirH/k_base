package com.buildware.kbase.project.web;

import com.buildware.kbase.project.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project management endpoints for listing, retrieving, creating, updating and deleting projects. This controller
 * returns public projects by default. Confidential projects are included when {@code includeConfidential=true} is
 * provided on the listing endpoint.
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectApiMapper mapper;

    /**
     * List projects.
     *
     * @param includeConfidential when true, includes confidential projects; otherwise returns only public
     * @return ordered list of projects
     */
    @GetMapping
    public List<ProjectDTO> list(
        @RequestParam(name = "includeConfidential", defaultValue = "false")
        boolean includeConfidential) {
        return projectService.listProjects(includeConfidential).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get a project by its code.
     *
     * @param code unique, URL-safe project code
     * @return 200 with the project when found, or 404 when missing
     */
    @GetMapping("/{code}")
    public ResponseEntity<ProjectDTO> getByCode(@PathVariable String code) {
        return projectService
            .getByCode(code)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new project.
     *
     * @param req request body containing code, name, basePath and optional fields
     * @return 201 with the created project
     */
    @PostMapping
    public ResponseEntity<ProjectDTO> create(@RequestBody @Valid ProjectUpsertDTO req) {
        var saved = projectService.create(mapper.toDomain(req));
        return ResponseEntity.status(201).body(mapper.toResponse(saved));
    }

    /**
     * Update an existing project by code.
     *
     * @param code unique project code
     * @param req  request body with fields to update
     * @return 200 with the updated project when found, or 404 when missing
     */
    @PutMapping("/{code}")
    public ResponseEntity<ProjectDTO> update(
        @PathVariable String code,
        @RequestBody @Valid ProjectUpsertDTO req) {
        var updates = mapper.toDomain(req);
        return projectService.update(code, updates)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a project by code.
     *
     * @param code unique project code
     * @return 204 when deletion is processed (idempotent)
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        projectService.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }

}
