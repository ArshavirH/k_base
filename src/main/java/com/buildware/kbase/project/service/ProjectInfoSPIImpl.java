package com.buildware.kbase.project.service;

import com.buildware.kbase.project.mapper.ProjectMapper;
import com.buildware.kbase.spi.ProjectInfoSPI;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectInfo;
import com.buildware.kbase.spi.ProjectInfoSPI.ProjectUpsert;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectInfoSPIImpl implements ProjectInfoSPI {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @Override
    public Optional<ProjectInfo> getByCode(String code) {
        return projectService.getByCode(code)
            .map(projectMapper::toView);
    }

    @Override
    public List<ProjectInfo> listAll() {
        return projectService.listProjects(false)
            .stream()
            .map(projectMapper::toView)
            .collect(Collectors.toList());
    }

    @Override
    public ProjectInfo create(ProjectUpsert upsert) {
        var saved = projectService.create(projectMapper.toDomain(upsert));
        return projectMapper.toView(saved);
    }

    @Override
    public Optional<ProjectInfo> update(String code, ProjectUpsert upsert) {
        var updates = projectMapper.toDomain(upsert);
        return projectService.update(code, updates).map(projectMapper::toView);
    }

    @Override
    public void deleteByCode(String code) {
        projectService.deleteByCode(code);
    }
}
