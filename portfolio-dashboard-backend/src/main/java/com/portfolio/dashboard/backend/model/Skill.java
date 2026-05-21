package com.portfolio.dashboard.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Technical or professional skill shown in the dashboard.")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique skill identifier.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Skill name.", example = "Spring Boot")
    private String name;

    @Schema(description = "Skill category.", example = "Backend")
    private String category;

    @Schema(description = "Proficiency percentage from 0 to 100.", example = "85", minimum = "0", maximum = "100")
    private int proficiency;

    @Schema(description = "Years of hands-on experience.", example = "3")
    private int yearsOfExperience;

    @Schema(description = "Most recent usage date or display label.", example = "Currently using")
    private String lastUsed;

    @Schema(description = "Frontend color class used when rendering the skill.", example = "bg-blue-500")
    private String color;
}
