package com.buildware.kbase.project.service;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.repository.ProjectRepository;
import com.buildware.kbase.spi.ProjectInfo;
import com.buildware.kbase.spi.ProjectLookupPort;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectLookupAdapter implements ProjectLookupPort {

    private final ProjectRepository projectRepository;

    @Override
    public Optional<ProjectInfo> getByCode(String code) {
        return projectRepository.findByCode(code).map(ProjectLookupAdapter::toInfo);
    }

    @Override
    public List<ProjectInfo> listAll() {
        return projectRepository.findAll().stream().map(ProjectLookupAdapter::toInfo).collect(Collectors.toList());
    }

    private static ProjectInfo toInfo(Project p) {
        return new ProjectInfo(p.getId(), p.getCode(), p.getBasePath());
    }
}

