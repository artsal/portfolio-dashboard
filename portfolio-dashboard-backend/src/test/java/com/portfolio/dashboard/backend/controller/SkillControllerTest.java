package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Skill;
import com.portfolio.dashboard.backend.service.SkillService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    void getAllSkillsReturnsSkills() {
        List<Skill> skills = List.of(skill("Java", 95));
        when(skillService.getAllSkills()).thenReturn(skills);

        ResponseEntity<List<Skill>> response = skillController.getAllSkills();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyElementsOf(skills);
    }

    @Test
    void getSkillByIdReturnsSkill() {
        Skill skill = skill("Spring", 90);
        when(skillService.getSkillById(2L)).thenReturn(skill);

        ResponseEntity<Skill> response = skillController.getSkillById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(skill);
    }

    @Test
    void createSkillReturnsCreatedSkill() {
        Skill input = skill("JUnit", 85);
        Skill created = skill("JUnit", 85);
        when(skillService.createSkill(input)).thenReturn(created);

        ResponseEntity<Skill> response = skillController.createSkill(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(created);
    }

    @Test
    void updateSkillReturnsUpdatedSkill() {
        Skill update = skill("Mockito", 80);
        Skill updated = skill("Mockito", 80);
        when(skillService.updateSkill(5L, update)).thenReturn(updated);

        ResponseEntity<Skill> response = skillController.updateSkill(5L, update);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(updated);
    }

    @Test
    void updateSkillReturnsNotFoundWhenServiceThrows() {
        Skill update = skill("Missing", 10);
        when(skillService.updateSkill(5L, update)).thenThrow(new RuntimeException("missing"));

        ResponseEntity<Skill> response = skillController.updateSkill(5L, update);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteSkillReturnsNoContent() {
        ResponseEntity<Void> response = skillController.deleteSkill(6L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(skillService).deleteSkill(6L);
    }

    @Test
    void getSkillStatsConvertsRowsToOrderedMap() {
        when(skillService.findAllSkillProficiencies()).thenReturn(List.<Object[]>of(
                new Object[]{"Java", 95},
                new Object[]{"React", 82}
        ));

        ResponseEntity<Map<String, Integer>> response = skillController.getSkillStats();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(
                Map.entry("Java", 95),
                Map.entry("React", 82)
        );
    }

    private Skill skill(String name, int proficiency) {
        return Skill.builder()
                .id(1L)
                .name(name)
                .category("Backend")
                .proficiency(proficiency)
                .yearsOfExperience(5)
                .lastUsed("Currently using")
                .color("bg-blue-500")
                .build();
    }
}
