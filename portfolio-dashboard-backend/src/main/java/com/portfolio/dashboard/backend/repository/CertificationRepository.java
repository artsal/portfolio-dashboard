package com.portfolio.dashboard.backend.repository;


import com.portfolio.dashboard.backend.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Optional<Certification> findTopByOrderByDateDesc();
}

