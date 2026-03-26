package com.portfolio.dashboard.backend.repository;

import com.portfolio.dashboard.backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // 🔹 For Overview page stats (latest project, total count, etc.)
    Optional<Project> findTopByOrderByStartDateDesc();

    // 🔹 For Projects chart: count projects per year
    @Query("SELECT FUNCTION('YEAR', p.startDate) AS year, COUNT(p) AS count FROM Project p GROUP BY FUNCTION('YEAR', p.startDate) ORDER BY FUNCTION('YEAR', p.startDate)")
    List<Object[]> countProjectsByYear();
}
