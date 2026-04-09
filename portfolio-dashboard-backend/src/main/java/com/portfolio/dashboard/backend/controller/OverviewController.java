package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Certification;
import com.portfolio.dashboard.backend.model.Project;
import com.portfolio.dashboard.backend.model.Skill;
import com.portfolio.dashboard.backend.repository.CertificationRepository;
import com.portfolio.dashboard.backend.repository.ProjectRepository;
import com.portfolio.dashboard.backend.repository.SkillRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired(required = false)
    private CertificationRepository certificationRepository;

    @GetMapping("/stats")
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
