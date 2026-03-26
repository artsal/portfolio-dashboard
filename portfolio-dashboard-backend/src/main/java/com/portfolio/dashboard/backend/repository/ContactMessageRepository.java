package com.portfolio.dashboard.backend.repository;


import com.portfolio.dashboard.backend.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}

