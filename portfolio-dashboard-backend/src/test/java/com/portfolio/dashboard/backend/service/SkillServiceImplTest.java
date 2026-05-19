package com.portfolio.dashboard.backend.service;

import com.portfolio.dashboard.backend.model.Skill;
import com.portfolio.dashboard.backend.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;

    private SkillServiceImpl skillService;

    @BeforeEach
    void setUp() {
        skillService = new SkillServiceImpl();
        ReflectionTestUtils.setField(skillService, "skillRepository", skillRepository);
    }

    @Test
    void getAllSkillsReturnsRepositoryResults() {
        List<Skill> skills = List.of(skill("Java", 90), skill("React", 80));
        when(skillRepository.findAll()).thenReturn(skills);

        assertThat(skillService.getAllSkills()).containsExactlyElementsOf(skills);
    }

    @Test
    void getSkillByIdReturnsSkillWhenFound() {
        Skill skill = skill("Spring Boot", 88);
        when(skillRepository.findById(8L)).thenReturn(Optional.of(skill));

        assertThat(skillService.getSkillById(8L)).isSameAs(skill);
    }

    @Test
    void getSkillByIdThrowsWhenMissing() {
        when(skillRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> skillService.getSkillById(8L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Skill not found with id 8");
    }

    @Test
    void createSkillSavesSkill() {
        Skill skill = skill("Mockito", 75);
        when(skillRepository.save(skill)).thenReturn(skill);

        assertThat(skillService.createSkill(skill)).isSameAs(skill);
        verify(skillRepository).save(skill);
    }

    @Test
    void updateSkillCopiesEditableFieldsAndSavesExistingSkill() {
        Skill existing = skill("Old", 10);
        Skill updates = Skill.builder()
                .name("JUnit")
                .category("Testing")
                .proficiency(92)
                .yearsOfExperience(3)
                .lastUsed("Currently using")
                .color("bg-green-500")
                .build();
        when(skillRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(skillRepository.save(existing)).thenReturn(existing);

        Skill result = skillService.updateSkill(5L, updates);

        assertThat(result).isSameAs(existing);
        assertThat(existing.getName()).isEqualTo("JUnit");
        assertThat(existing.getCategory()).isEqualTo("Testing");
        assertThat(existing.getProficiency()).isEqualTo(92);
        assertThat(existing.getYearsOfExperience()).isEqualTo(3);
        assertThat(existing.getLastUsed()).isEqualTo("Currently using");
        assertThat(existing.getColor()).isEqualTo("bg-green-500");
        verify(skillRepository).save(existing);
    }

    @Test
    void updateSkillThrowsWhenMissing() {
        when(skillRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> skillService.updateSkill(10L, skill("Missing", 1)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Skill not found with id 10");
    }

    @Test
    void deleteSkillDelegatesToRepository() {
        skillService.deleteSkill(6L);

        verify(skillRepository).deleteById(6L);
    }

    @Test
    void findAllSkillProficienciesReturnsRepositoryResults() {
        List<Object[]> rows = List.<Object[]>of(new Object[]{"Java", 90});
        when(skillRepository.findAllSkillProficiencies()).thenReturn(rows);

        assertThat(skillService.findAllSkillProficiencies()).containsExactlyElementsOf(rows);
    }

    private Skill skill(String name, int proficiency) {
        return Skill.builder()
                .id(1L)
                .name(name)
                .category("Backend")
                .proficiency(proficiency)
                .yearsOfExperience(4)
                .lastUsed("2026-05-01")
                .color("bg-blue-500")
                .build();
    }
}
