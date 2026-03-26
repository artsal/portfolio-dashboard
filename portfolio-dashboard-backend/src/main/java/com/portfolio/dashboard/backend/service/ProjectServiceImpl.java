package com.portfolio.dashboard.backend.service;

import com.portfolio.dashboard.backend.model.Project;
import com.portfolio.dashboard.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {


    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project updateProject(Long id, Project project) {
        return projectRepository.findById(id).map(existingProject -> {
            existingProject.setTitle(project.getTitle());
            existingProject.setDescription(project.getDescription());
            existingProject.setTechStack(project.getTechStack());
            existingProject.setStatus(project.getStatus());
            existingProject.setStartDate(project.getStartDate());
            existingProject.setEndDate(project.getEndDate());
            existingProject.setGithubLink(project.getGithubLink());
            return projectRepository.save(existingProject);
        }).orElseThrow(() -> new RuntimeException("Project not found with id " + id));
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public List<Object[]> countProjectsByYear() {
        return projectRepository.countProjectsByYear();
    }
}