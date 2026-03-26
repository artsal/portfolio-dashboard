package com.portfolio.dashboard.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;


@Entity
@Table(name = "certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String issuingOrganization;
    private LocalDate date;
    private String credentialUrl;
}
