package com.buildware.kbase.project.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectUpsert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProjectMapper {

    ProjectInfo toView(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastSyncAt", ignore = true)
    Project toDomain(ProjectUpsert upsert);
}
