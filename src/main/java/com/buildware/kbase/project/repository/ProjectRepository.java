package com.buildware.kbase.project.repository;

import com.buildware.kbase.project.domain.Project;
import com.buildware.kbase.project.domain.Project.Visibility;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findByCode(String code);

    boolean existsByCode(String code);

    List<Project> findAllByVisibilityNot(Visibility visibility);
}
