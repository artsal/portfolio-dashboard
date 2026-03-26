package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Certification;
import com.portfolio.dashboard.backend.repository.CertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/certifications")
public class CertificationController {

    @Autowired
    private CertificationRepository certificationRepository;

    @GetMapping
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    @PostMapping
    public Certification addCertification(@RequestBody Certification certification) {
        return certificationRepository.save(certification);
    }

    @GetMapping("/{id}")
    public Optional<Certification> getCertificationById(@PathVariable Long id) {
        return certificationRepository.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCertification(@PathVariable Long id) {
        certificationRepository.deleteById(id);
    }
}