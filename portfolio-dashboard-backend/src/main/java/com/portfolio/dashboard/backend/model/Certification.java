package com.portfolio.dashboard.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Professional certification or credential.")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique certification identifier.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Certification name.", example = "AWS Certified Cloud Practitioner")
    private String name;

    @Schema(description = "Organization that issued the certification.", example = "Amazon Web Services")
    private String issuingOrganization;

    @Schema(description = "Date the certification was earned.", example = "2025-08-01")
    private LocalDate date;

    @Schema(description = "Credential verification URL.", example = "https://www.credly.com/badges/example")
    private String credentialUrl;
}
