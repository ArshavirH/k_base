package com.buildware.kbase.project.service;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.domain.Project.Visibility;
import com.buildware.kbase.project.repository.ProjectRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<Project> listProjects(boolean includeConfidential) {
        List<Project> all = includeConfidential
            ? projectRepository.findAll()
            : projectRepository.findAllByVisibilityNot(Visibility.CONFIDENTIAL);
        return all.stream()
            .sorted(Comparator.comparing(Project::getCode))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Project> getByCode(String code) {
        return projectRepository.findByCode(code);
    }

    // Filesystem project discovery removed as part of deprecating FS-based sync.
}
