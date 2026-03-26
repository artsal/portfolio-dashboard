package com.portfolio.dashboard.backend.service;

import com.portfolio.dashboard.backend.model.Skill;
import java.util.List;

public interface SkillService {
    List<Skill> getAllSkills();
    Skill getSkillById(Long id);
    Skill createSkill(Skill skill);
    Skill updateSkill(Long id, Skill updatedSkill);
    void deleteSkill(Long id);
    List<Object[]> findAllSkillProficiencies();
}
