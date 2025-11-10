package com.buildware.kbase.project.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProjectMapper {

    ProjectInfo toView(Project project);
}
