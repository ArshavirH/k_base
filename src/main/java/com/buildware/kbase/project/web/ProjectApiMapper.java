package com.buildware.kbase.project.web;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.buildware.kbase.project.domain.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProjectApiMapper {

    ProjectDTO toResponse(Project project);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastSyncAt", ignore = true)
    Project toDomain(ProjectUpsertDTO request);
}
