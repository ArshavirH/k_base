package com.buildware.kbase.spi;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectInfoSPI {

    Optional<ProjectInfo> getByCode(String code);

    List<ProjectInfo> listAll();

    /**
     * Create a new project.
     *
     * @param upsert project properties
     * @return created project view
     */
    ProjectInfo create(ProjectUpsert upsert);

    /**
     * Update an existing project identified by code.
     *
     * @param code unique project code
     * @param upsert fields to update
     * @return updated project view when found
     */
    Optional<ProjectInfo> update(String code, ProjectUpsert upsert);

    /**
     * Delete a project by its code. No-op when not found.
     *
     * @param code unique project code
     */
    void deleteByCode(String code);

    record ProjectInfo(UUID id, String code, String basePath) {

    }

    record ProjectUpsert(
        String code,
        @NotBlank String name,
        @NotBlank String basePath,
        List<String> domainTags,
        String description,
        Visibility visibility
    ) {

    }

    enum Visibility {
        PUBLIC,
        CONFIDENTIAL
    }
}
