package com.portfolio.dashboard.backend.repository;

import com.portfolio.dashboard.backend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // 🔹 For Overview stats
    List<Skill> findTop3ByOrderByProficiencyDesc();

    // 🔹 For Skills chart: get skill name and proficiency
    @Query("SELECT s.name, s.proficiency FROM Skill s")
    List<Object[]> findAllSkillProficiencies();
}
