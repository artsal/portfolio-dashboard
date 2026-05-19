package com.portfolio.dashboard.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class HelloControllerTest {

    private HelloController helloController;

    @BeforeEach
    void setUp() {
        helloController = new HelloController();
        ReflectionTestUtils.setField(helloController, "author", "Arthur");
    }

    @Test
    void greetUserIncludesConfiguredAuthor() {
        assertThat(helloController.greetUser())
                .isEqualTo("<h1>Hey Arthur, welcome to Portfolio Dashboard Backend!!! </h1>");
    }
}
