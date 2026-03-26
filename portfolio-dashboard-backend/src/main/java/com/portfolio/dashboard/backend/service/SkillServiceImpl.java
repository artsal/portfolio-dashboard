package com.portfolio.dashboard.backend.service;

import com.portfolio.dashboard.backend.model.Project;
import com.portfolio.dashboard.backend.model.Skill;
import com.portfolio.dashboard.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> new RuntimeException("Skill not found with id " + id));
    }

    @Override
    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public Skill updateSkill(Long id, Skill skill) {
        return skillRepository.findById(id).map(existingSkill -> {
            existingSkill.setName(skill.getName());
            existingSkill.setCategory(skill.getCategory());
            existingSkill.setProficiency(skill.getProficiency());
            existingSkill.setYearsOfExperience(skill.getYearsOfExperience());
            existingSkill.setLastUsed(skill.getLastUsed());
            existingSkill.setColor(skill.getColor());
            return skillRepository.save(existingSkill);
        }).orElseThrow(() -> new RuntimeException("Skill not found with id " + id));
    }

    @Override
    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    @Override
    public List<Object[]> findAllSkillProficiencies() {
        return skillRepository.findAllSkillProficiencies();
    }

}
