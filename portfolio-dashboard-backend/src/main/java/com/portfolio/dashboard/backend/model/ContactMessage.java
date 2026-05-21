package com.portfolio.dashboard.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contact_messages")
@Schema(description = "Stored contact form submission and delivery status.")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique contact message identifier.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Sender name.", example = "Jane Recruiter")
    private String name;

    @Schema(description = "Sender email address.", example = "jane@example.com")
    private String email;

    @Column(length = 2000)
    @Schema(description = "Message body.", example = "I'd like to discuss a Java developer opportunity.")
    private String message;

    @Schema(description = "Submission timestamp.", example = "2026-05-20T13:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "Delivery status.", example = "SENT", allowableValues = {"PENDING", "SENT", "FAILED", "SPAM"})
    private String status;
}
