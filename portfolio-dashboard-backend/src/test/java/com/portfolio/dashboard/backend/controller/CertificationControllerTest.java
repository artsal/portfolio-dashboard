package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Certification;
import com.portfolio.dashboard.backend.repository.CertificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificationControllerTest {

    @Mock
    private CertificationRepository certificationRepository;

    private CertificationController certificationController;

    @BeforeEach
    void setUp() {
        certificationController = new CertificationController();
        ReflectionTestUtils.setField(certificationController, "certificationRepository", certificationRepository);
    }

    @Test
    void getAllCertificationsReturnsRepositoryResults() {
        List<Certification> certifications = List.of(certification("AWS"));
        when(certificationRepository.findAll()).thenReturn(certifications);

        assertThat(certificationController.getAllCertifications()).containsExactlyElementsOf(certifications);
    }

    @Test
    void addCertificationSavesCertification() {
        Certification certification = certification("Azure");
        when(certificationRepository.save(certification)).thenReturn(certification);

        assertThat(certificationController.addCertification(certification)).isSameAs(certification);
        verify(certificationRepository).save(certification);
    }

    @Test
    void getCertificationByIdReturnsRepositoryResult() {
        Certification certification = certification("Java");
        when(certificationRepository.findById(9L)).thenReturn(Optional.of(certification));

        assertThat(certificationController.getCertificationById(9L)).contains(certification);
    }

    @Test
    void deleteCertificationDelegatesToRepository() {
        certificationController.deleteCertification(3L);

        verify(certificationRepository).deleteById(3L);
    }

    private Certification certification(String name) {
        return Certification.builder()
                .id(1L)
                .name(name)
                .issuingOrganization("Issuer")
                .date(LocalDate.of(2026, 1, 1))
                .credentialUrl("https://example.com/cert")
                .build();
    }
}
