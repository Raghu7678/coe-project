package com.jml.reconciliation.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(originPatterns = "*")
public class RootController {

    @GetMapping("/")
    public Map<String, String> getRootStatus() {
        return Map.of(
                "service", "JML Access Reconciliation Engine API",
                "status", "UP",
                "documentation", "/h2-console",
                "version", "1.0.0"
        );
    }
}
