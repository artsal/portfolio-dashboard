package com.portfolio.dashboard.backend.service;

import com.portfolio.dashboard.backend.model.Project;
import com.portfolio.dashboard.backend.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl();
        ReflectionTestUtils.setField(projectService, "projectRepository", projectRepository);
    }

    @Test
    void createProjectSavesProject() {
        Project project = project("Portfolio Dashboard");
        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectService.createProject(project);

        assertThat(result).isSameAs(project);
        verify(projectRepository).save(project);
    }

    @Test
    void getProjectByIdReturnsRepositoryResult() {
        Project project = project("API");
        when(projectRepository.findById(7L)).thenReturn(Optional.of(project));

        assertThat(projectService.getProjectById(7L)).contains(project);
    }

    @Test
    void getAllProjectsReturnsRepositoryResults() {
        List<Project> projects = List.of(project("One"), project("Two"));
        when(projectRepository.findAll()).thenReturn(projects);

        assertThat(projectService.getAllProjects()).containsExactlyElementsOf(projects);
    }

    @Test
    void updateProjectCopiesEditableFieldsAndSavesExistingProject() {
        Project existing = project("Old");
        Project updates = Project.builder()
                .title("New")
                .description("New description")
                .techStack("Spring,React")
                .status("Completed")
                .startDate(LocalDate.of(2026, 1, 15))
                .endDate("2026-04-30")
                .githubLink("https://github.com/example/new")
                .build();
        when(projectRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(existing)).thenReturn(existing);

        Project result = projectService.updateProject(3L, updates);

        assertThat(result).isSameAs(existing);
        assertThat(existing.getTitle()).isEqualTo("New");
        assertThat(existing.getDescription()).isEqualTo("New description");
        assertThat(existing.getTechStack()).isEqualTo("Spring,React");
        assertThat(existing.getStatus()).isEqualTo("Completed");
        assertThat(existing.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(existing.getEndDate()).isEqualTo("2026-04-30");
        assertThat(existing.getGithubLink()).isEqualTo("https://github.com/example/new");
        verify(projectRepository).save(existing);
    }

    @Test
    void updateProjectThrowsWhenProjectDoesNotExist() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProject(99L, project("Missing")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Project not found with id 99");
    }

    @Test
    void deleteProjectDelegatesToRepository() {
        projectService.deleteProject(4L);

        verify(projectRepository).deleteById(4L);
    }

    @Test
    void countProjectsByYearReturnsRepositoryResults() {
        List<Object[]> rows = List.<Object[]>of(new Object[]{2026, 2L});
        when(projectRepository.countProjectsByYear()).thenReturn(rows);

        assertThat(projectService.countProjectsByYear()).containsExactlyElementsOf(rows);
    }

    private Project project(String title) {
        return Project.builder()
                .id(1L)
                .title(title)
                .description("Description")
                .techStack("Java")
                .status("Active")
                .startDate(LocalDate.of(2025, 10, 1))
                .endDate("2025-12-31")
                .githubLink("https://github.com/example/project")
                .build();
    }
}
