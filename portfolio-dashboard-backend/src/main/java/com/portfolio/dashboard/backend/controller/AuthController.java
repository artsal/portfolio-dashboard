package com.portfolio.dashboard.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/validate")
    public ResponseEntity<String> validate() {
        return ResponseEntity.ok("Valid credentials");
    }
}