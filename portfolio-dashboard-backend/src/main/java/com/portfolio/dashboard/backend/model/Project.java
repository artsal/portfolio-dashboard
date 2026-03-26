package com.portfolio.dashboard.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    // store techs as CSV (example: "React,Spring Boot,MySQL")
    private String techStack;

    private String status; // Planned, Active, Completed


    @Column(name = "start_date")
    private LocalDate startDate; // ISO string yyyy-MM-dd (or change to LocalDate later)

    private String endDate;

    private String githubLink;
}
