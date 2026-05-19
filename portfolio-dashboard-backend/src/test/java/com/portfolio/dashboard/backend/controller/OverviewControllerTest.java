package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Certification;
import com.portfolio.dashboard.backend.model.Project;
import com.portfolio.dashboard.backend.model.Skill;
import com.portfolio.dashboard.backend.repository.CertificationRepository;
import com.portfolio.dashboard.backend.repository.ProjectRepository;
import com.portfolio.dashboard.backend.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OverviewControllerTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private CertificationRepository certificationRepository;

    private OverviewController overviewController;

    @BeforeEach
    void setUp() {
        overviewController = new OverviewController();
        ReflectionTestUtils.setField(overviewController, "projectRepository", projectRepository);
        ReflectionTestUtils.setField(overviewController, "skillRepository", skillRepository);
        ReflectionTestUtils.setField(overviewController, "certificationRepository", certificationRepository);
    }

    @Test
    void getOverviewStatsReturnsCountsAndLatestValues() {
        when(projectRepository.count()).thenReturn(4L);
        when(projectRepository.findTopByOrderByStartDateDesc()).thenReturn(Optional.of(
                Project.builder().title("Latest Project").build()
        ));
        when(skillRepository.count()).thenReturn(6L);
        when(skillRepository.findTop3ByOrderByProficiencyDesc()).thenReturn(List.of(
                Skill.builder().name("Java").build(),
                Skill.builder().name("Spring").build(),
                Skill.builder().name("React").build()
        ));
        when(certificationRepository.count()).thenReturn(2L);
        when(certificationRepository.findTopByOrderByDateDesc()).thenReturn(Optional.of(
                Certification.builder().name("Cloud Cert").date(LocalDate.of(2026, 1, 1)).build()
        ));

        Map<String, Object> stats = overviewController.getOverviewStats();

        assertThat(stats).containsKeys("projects", "skills", "experience", "certifications");
        assertThat(stats.get("projects")).isEqualTo(Map.of("count", 4L, "latest", "Latest Project"));
        assertThat(stats.get("skills")).isEqualTo(Map.of("count", 6L, "top", List.of("Java", "Spring", "React")));
        assertThat(stats.get("experience")).isEqualTo(Map.of("years", 16));
        assertThat(stats.get("certifications")).isEqualTo(Map.of("count", 2L, "latest", "Cloud Cert"));
    }

    @Test
    void getOverviewStatsUsesFallbacksWhenLatestValuesAreMissing() {
        when(projectRepository.count()).thenReturn(0L);
        when(projectRepository.findTopByOrderByStartDateDesc()).thenReturn(Optional.empty());
        when(skillRepository.count()).thenReturn(0L);
        when(skillRepository.findTop3ByOrderByProficiencyDesc()).thenReturn(List.of());
        when(certificationRepository.count()).thenReturn(0L);
        when(certificationRepository.findTopByOrderByDateDesc()).thenReturn(Optional.empty());

        Map<String, Object> stats = overviewController.getOverviewStats();

        assertThat(stats.get("projects")).isEqualTo(Map.of("count", 0L, "latest", "No projects yet"));
        assertThat(stats.get("skills")).isEqualTo(Map.of("count", 0L, "top", List.of()));
        assertThat(stats.get("certifications")).isEqualTo(Map.of("count", 0L, "latest", "None"));
    }

    @Test
    void getOverviewStatsHandlesMissingCertificationRepository() {
        ReflectionTestUtils.setField(overviewController, "certificationRepository", null);
        when(projectRepository.count()).thenReturn(1L);
        when(projectRepository.findTopByOrderByStartDateDesc()).thenReturn(Optional.empty());
        when(skillRepository.count()).thenReturn(1L);
        when(skillRepository.findTop3ByOrderByProficiencyDesc()).thenReturn(List.of());

        Map<String, Object> stats = overviewController.getOverviewStats();

        assertThat(stats.get("certifications")).isEqualTo(Map.of("count", 0L, "latest", "None"));
    }
}
