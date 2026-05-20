package com.portfolio.dashboard.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Simple backend greeting and smoke-test endpoints.")
public class HelloController {

    @Value("${author.name}")
    private String author;

    @GetMapping("/hello")
    @Operation(
            summary = "Get backend greeting",
            description = "Returns a simple HTML greeting that can be used as a lightweight backend smoke test.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Greeting returned",
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "<h1>Hey John, welcome to Portfolio Dashboard Backend!!! </h1>"))))
    public String greetUser() {
        return "<h1>Hey " + author + ", welcome to Portfolio Dashboard Backend!!! </h1>";
    }
}
