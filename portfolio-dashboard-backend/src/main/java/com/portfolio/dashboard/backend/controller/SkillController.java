package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Skill;
import com.portfolio.dashboard.backend.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }


    @GetMapping
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @PostMapping
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        return ResponseEntity.ok(skillService.createSkill(skill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        try {
            Skill updatedSkill = skillService.updateSkill(id, skill);
            return ResponseEntity.ok(updatedSkill);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Chart endpoint: skills by proficiency
    @GetMapping("/stats")
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