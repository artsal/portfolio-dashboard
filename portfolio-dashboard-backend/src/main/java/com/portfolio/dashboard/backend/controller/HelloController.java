package com.portfolio.dashboard.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${author.name}")
    private String author;


    @GetMapping("/hello")
    public String greetUser() {
        return "<h1>Hey " + author + ", welcome to Portfolio Dashboard Backend!!! </h1>";
    }
}
