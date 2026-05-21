package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Project;
import com.portfolio.dashboard.backend.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Manage portfolio projects and project chart data.")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(
            summary = "List projects",
            description = "Returns every portfolio project ordered by the service and repository defaults.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Projects returned successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Project.class)))))
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @PostMapping
    @Operation(
            summary = "Create project",
            description = "Creates a new portfolio project. Requires admin HTTP Basic authentication.",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Project created",
                            content = @Content(schema = @Schema(implementation = Project.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
            })
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get project",
            description = "Returns one project by its database identifier.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project found",
                            content = @Content(schema = @Schema(implementation = Project.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
            })
    public ResponseEntity<Project> getProjectById(
            @Parameter(description = "Project identifier", example = "1")
            @PathVariable Long id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update project",
            description = "Updates an existing portfolio project. Requires admin HTTP Basic authentication.",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project updated",
                            content = @Content(schema = @Schema(implementation = Project.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
            })
    public ResponseEntity<Project> updateProject(
            @Parameter(description = "Project identifier", example = "1")
            @PathVariable Long id,
            @RequestBody Project project) {
        try {
            Project updatedProject = projectService.updateProject(id, project);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete project",
            description = "Deletes a portfolio project by identifier. Requires admin HTTP Basic authentication.",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Project deleted", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
            })
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Project identifier", example = "1")
            @PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Chart endpoint: count projects per year
    @GetMapping("/stats")
    @Operation(
            summary = "Get project statistics",
            description = "Returns project counts grouped by project start year for dashboard charting.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Project statistics returned",
                    content = @Content(
                            schema = @Schema(type = "object", description = "Map of year to project count"),
                            examples = @ExampleObject(value = "{\"2024\":2,\"2025\":4}"))))
    public ResponseEntity<Map<String, Long>> getProjectStats() {
        List<Object[]> results = projectService.countProjectsByYear();
        Map<String, Long> stats = new LinkedHashMap<>();

        for (Object[] row : results) {
            Integer year = (Integer) row[0];
            Long count = (Long) row[1];
            stats.put(String.valueOf(year), count);
        }

        return ResponseEntity.ok(stats);
    }
}