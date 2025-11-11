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
import org.apache.commons.lang3.StringUtils;
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

    @Transactional
    public Project create(Project project) {
        validateNewProject(project);
        if (projectRepository.existsByCode(project.getCode())) {
            throw new IllegalArgumentException("Project code already exists: " + project.getCode());
        }
        if (project.getVisibility() == null) {
            project.setVisibility(Visibility.PUBLIC);
        }
        return projectRepository.save(project);
    }

    @Transactional
    public Optional<Project> update(String code, Project update) {
        return projectRepository.findByCode(code).map(existing -> {
            if (StringUtils.isNotBlank(update.getName())) {
                existing.setName(update.getName());
            }
            if (StringUtils.isNotBlank(update.getBasePath())) {
                existing.setBasePath(update.getBasePath());
            }
            if (update.getDomainTags() != null) {
                existing.setDomainTags(update.getDomainTags());
            }
            if (update.getDescription() != null) {
                existing.setDescription(update.getDescription());
            }
            if (update.getVisibility() != null) {
                existing.setVisibility(update.getVisibility());
            }
            return projectRepository.save(existing);
        });
    }

    @Transactional
    public void deleteByCode(String code) {
        projectRepository.findByCode(code)
            .ifPresent(it -> projectRepository.deleteById(it.getId()));
    }

    private static void validateNewProject(Project p) {
        if (p == null) {
            throw new IllegalArgumentException("Project must not be null");
        }
        if (StringUtils.isBlank(p.getCode())) {
            throw new IllegalArgumentException("Project code must not be blank");
        }
        if (StringUtils.isBlank(p.getName())) {
            throw new IllegalArgumentException("Project name must not be blank");
        }
        if (StringUtils.isBlank(p.getBasePath())) {
            throw new IllegalArgumentException("Project basePath must not be blank");
        }
    }
}
