package com.portfolio.dashboard.backend.service;

import com.portfolio.dashboard.backend.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project createProject(Project project);

    Optional<Project> getProjectById(Long id);

    List<Project> getAllProjects();

    Project updateProject(Long id, Project project);

    void deleteProject(Long id);

    List<Object[]> countProjectsByYear();
}
