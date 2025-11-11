package com.buildware.kbase.project.web;

import com.buildware.kbase.project.web.ProjectDTO.VisibilityDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Project upsert payload used for create and update operations.
 */
@Getter
@Setter
public class ProjectUpsertDTO {

    @Size(min = 1, max = 128)
    private String code;

    @NotBlank
    @Size(max = 256)
    private String name;

    @NotBlank
    @Size(max = 1024)
    private String basePath;

    private List<String> domainTags;

    @Size(max = 1024)
    private String description;

    private VisibilityDTO visibility;
}
