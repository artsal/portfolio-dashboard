package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Certification;
import com.portfolio.dashboard.backend.model.Project;
import com.portfolio.dashboard.backend.model.Skill;
import com.portfolio.dashboard.backend.repository.CertificationRepository;
import com.portfolio.dashboard.backend.repository.ProjectRepository;
import com.portfolio.dashboard.backend.repository.SkillRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/overview")
@Tag(name = "Overview", description = "Dashboard summary metrics for the portfolio landing view.")
public class OverviewController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired(required = false)
    private CertificationRepository certificationRepository;

    @GetMapping("/stats")
    @Operation(
            summary = "Get overview statistics",
            description = "Returns aggregate counts and highlights for projects, skills, experience, and certifications.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Overview statistics returned",
                    content = @Content(
                            schema = @Schema(type = "object", description = "Nested overview statistics object"),
                            examples = @ExampleObject(value = "{\"projects\":{\"count\":4,\"latest\":\"Portfolio Dashboard\"},\"skills\":{\"count\":10,\"top\":[\"React\",\"Spring Boot\",\"MySQL\"]},\"experience\":{\"years\":16},\"certifications\":{\"count\":2,\"latest\":\"AWS Cloud Practitioner\"}}"))))
    public Map<String, Object> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();

        // 🧱 Projects
        long projectCount = projectRepository.count();
        Optional<Project> latestProjectOpt = projectRepository.findTopByOrderByStartDateDesc();
        String latestProject = latestProjectOpt.map(Project::getTitle).orElse("No projects yet");

        stats.put("projects", Map.of(
                "count", projectCount,
                "latest", latestProject
                                    ));

        // ⚙️ Skills
        long skillCount = skillRepository.count();
        List<Skill> topSkills = skillRepository.findTop3ByOrderByProficiencyDesc();
        List<String> topSkillNames = new ArrayList<>();
        for (Skill s : topSkills) {
            topSkillNames.add(s.getName());
        }
        stats.put("skills", Map.of(
                "count", skillCount,
                "top", topSkillNames
                                  ));

        // ⏳ Experience
        int yearsExperience = 16; // static for now
        stats.put("experience", Map.of("years", yearsExperience));

        // 🏆 Certifications
        long certCount = 0;
        String latestCert = "None";
        if (certificationRepository != null) {
            certCount = certificationRepository.count();
            Optional<Certification> latestCertOpt = certificationRepository.findTopByOrderByDateDesc();
            latestCert = latestCertOpt.map(Certification::getName).orElse("None");
        }
        stats.put("certifications", Map.of(
                "count", certCount,
                "latest", latestCert
                                          ));

        return stats;
    }
}
