package com.portfolio.dashboard.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Contact form payload submitted from the portfolio frontend.")
public class ContactRequest {

    @Schema(description = "Sender name.", example = "Jane Recruiter")
    private String name;

    @Schema(description = "Sender email address.", example = "jane@example.com")
    private String email;

    @Schema(description = "Message body.", example = "I'd like to discuss a Java developer opportunity.")
    private String message;

    @Schema(description = "Hidden honeypot field used to detect spam. Real users should leave this empty.", example = "")
    private String website;
}
