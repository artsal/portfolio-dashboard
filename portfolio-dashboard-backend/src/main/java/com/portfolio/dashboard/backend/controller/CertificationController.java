package com.portfolio.dashboard.backend.controller;

import com.portfolio.dashboard.backend.model.Certification;
import com.portfolio.dashboard.backend.repository.CertificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/certifications")
@Tag(name = "Certifications", description = "Manage professional certifications shown in the portfolio dashboard.")
public class CertificationController {

    @Autowired
    private CertificationRepository certificationRepository;

    @GetMapping
    @Operation(
            summary = "List certifications",
            description = "Returns all professional certifications stored for the portfolio.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Certifications returned successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Certification.class)))))
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    @PostMapping
    @Operation(
            summary = "Create certification",
            description = "Creates a new certification entry. Requires admin HTTP Basic authentication.",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certification created",
                            content = @Content(schema = @Schema(implementation = Certification.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
            })
    public Certification addCertification(@RequestBody Certification certification) {
        return certificationRepository.save(certification);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get certification",
            description = "Returns one certification by its database identifier.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certification lookup completed",
                            content = @Content(schema = @Schema(implementation = Certification.class))),
                    @ApiResponse(responseCode = "404", description = "Certification not found", content = @Content)
            })
    public Optional<Certification> getCertificationById(
            @Parameter(description = "Certification identifier", example = "1")
            @PathVariable Long id) {
        return certificationRepository.findById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete certification",
            description = "Deletes a certification by identifier. Requires admin HTTP Basic authentication.",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Certification deletion requested", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content)
            })
    public void deleteCertification(
            @Parameter(description = "Certification identifier", example = "1")
            @PathVariable Long id) {
        certificationRepository.deleteById(id);
    }
}
