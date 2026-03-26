package com.portfolio.dashboard.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g. "React", "Spring Boot"

    private String category; // e.g. "Frontend", "Backend", "Database"

    private int proficiency; // percentage 0–100

    private int yearsOfExperience;

    private String lastUsed; // e.g. "2025-10-01" or "Currently using"

    private String color; // Color for frontend display (like "bg-blue-500")
}