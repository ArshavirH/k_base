package com.buildware.kbase.project.web.mapper;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.web.dto.ProjectDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDTO toResponse(Project project);
}
