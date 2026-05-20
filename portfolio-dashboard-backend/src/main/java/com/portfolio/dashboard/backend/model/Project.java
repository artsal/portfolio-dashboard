package com.portfolio.dashboard.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Portfolio project shown in the dashboard.")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique project identifier.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Project title.", example = "Portfolio Dashboard")
    private String title;

    @Column(length = 2000)
    @Schema(description = "Long-form project description.", example = "A full-stack dashboard for showcasing projects and skills.")
    private String description;

    @Schema(description = "Comma-separated technology stack.", example = "React,Spring Boot,MySQL")
    private String techStack;

    @Schema(description = "Project lifecycle status.", example = "Completed", allowableValues = {"Planned", "Active", "Completed"})
    private String status;

    @Column(name = "start_date")
    @Schema(description = "Project start date in ISO-8601 format.", example = "2025-01-15")
    private LocalDate startDate;

    @Schema(description = "Project end date or display label.", example = "2025-04-30")
    private String endDate;

    @Schema(description = "Public source repository URL.", example = "https://github.com/artsal/portfolio-dashboard")
    private String githubLink;
}
