package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Skill;
import com.portfolio.dashboard.backend.service.SkillService;
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

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "Manage skills and skill proficiency chart data.")
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @Operation(
            summary = "List skills",
            description = "Returns all skills displayed in the portfolio dashboard.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Skills returned successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Skill.class)))))
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get skill",
            description = "Returns one skill by its database identifier.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Skill returned",
                    content = @Content(schema = @Schema(implementation = Skill.class))))
    public ResponseEntity<Skill> getSkillById(
            @Parameter(description = "Skill identifier", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create skill",
            description = "Creates a new skill entry. Requires admin HTTP Basic authentication.",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Skill created",
                            content = @Content(schema = @Schema(implementation = Skill.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
            })
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        return ResponseEntity.ok(skillService.createSkill(skill));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update skill",
            description = "Updates an existing skill entry. Requires admin HTTP Basic authentication.",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Skill updated",
                            content = @Content(schema = @Schema(implementation = Skill.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Skill not found", content = @Content)
            })
    public ResponseEntity<Skill> updateSkill(
            @Parameter(description = "Skill identifier", example = "1")
            @PathVariable Long id,
            @RequestBody Skill skill) {
        try {
            Skill updatedSkill = skillService.updateSkill(id, skill);
            return ResponseEntity.ok(updatedSkill);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete skill",
            description = "Deletes a skill by identifier. Requires admin HTTP Basic authentication.",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Skill deleted", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
            })
    public ResponseEntity<Void> deleteSkill(
            @Parameter(description = "Skill identifier", example = "1")
            @PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Chart endpoint: skills by proficiency
    @GetMapping("/stats")
    @Operation(
            summary = "Get skill statistics",
            description = "Returns skill names mapped to proficiency percentages for dashboard charting.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Skill statistics returned",
                    content = @Content(
                            schema = @Schema(type = "object", description = "Map of skill name to proficiency percentage"),
                            examples = @ExampleObject(value = "{\"React\":90,\"Spring Boot\":85}"))))
    public ResponseEntity<Map<String, Integer>> getSkillStats() {
        List<Object[]> results = skillService.findAllSkillProficiencies();
        Map<String, Integer> stats = new LinkedHashMap<>();

        for (Object[] row : results) {
            String name = (String) row[0];
            Integer proficiency = (Integer) row[1];
            stats.put(name, proficiency);
        }

        return ResponseEntity.ok(stats);
    }
}
