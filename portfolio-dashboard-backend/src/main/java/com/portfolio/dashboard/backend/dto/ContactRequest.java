package com.portfolio.dashboard.backend.dto;

import lombok.Data;

@Data
public class ContactRequest {
    private String name;
    private String email;
    private String message;
    // 🕵️‍♂️ hidden field for spam filtering
    private String website;
}
