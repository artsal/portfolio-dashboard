package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Project;
import com.portfolio.dashboard.backend.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @Test
    void getAllProjectsReturnsProjects() {
        List<Project> projects = List.of(project("Portfolio Dashboard"));
        when(projectService.getAllProjects()).thenReturn(projects);

        ResponseEntity<List<Project>> response = projectController.getAllProjects();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyElementsOf(projects);
    }

    @Test
    void createProjectReturnsCreatedProject() {
        Project input = project("Input");
        Project created = project("Created");
        when(projectService.createProject(input)).thenReturn(created);

        ResponseEntity<Project> response = projectController.createProject(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(created);
    }

    @Test
    void getProjectByIdReturnsProjectWhenFound() {
        Project project = project("Found");
        when(projectService.getProjectById(2L)).thenReturn(Optional.of(project));

        ResponseEntity<Project> response = projectController.getProjectById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(project);
    }

    @Test
    void getProjectByIdReturnsNotFoundWhenMissing() {
        when(projectService.getProjectById(2L)).thenReturn(Optional.empty());

        ResponseEntity<Project> response = projectController.getProjectById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void updateProjectReturnsUpdatedProject() {
        Project update = project("Update");
        Project updated = project("Updated");
        when(projectService.updateProject(3L, update)).thenReturn(updated);

        ResponseEntity<Project> response = projectController.updateProject(3L, update);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(updated);
    }

    @Test
    void updateProjectReturnsNotFoundWhenServiceThrows() {
        Project update = project("Missing");
        when(projectService.updateProject(3L, update)).thenThrow(new RuntimeException("missing"));

        ResponseEntity<Project> response = projectController.updateProject(3L, update);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteProjectReturnsNoContent() {
        ResponseEntity<Void> response = projectController.deleteProject(4L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(projectService).deleteProject(4L);
    }

    @Test
    void getProjectStatsConvertsRowsToOrderedMap() {
        when(projectService.countProjectsByYear()).thenReturn(List.<Object[]>of(
                new Object[]{2025, 1L},
                new Object[]{2026, 3L}
        ));

        ResponseEntity<Map<String, Long>> response = projectController.getProjectStats();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(
                Map.entry("2025", 1L),
                Map.entry("2026", 3L)
        );
    }

    private Project project(String title) {
        return Project.builder()
                .id(1L)
                .title(title)
                .description("Description")
                .techStack("Java")
                .status("Active")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate("2026-02-01")
                .githubLink("https://github.com/example/project")
                .build();
    }
}
