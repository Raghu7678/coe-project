package com.jml.reconciliation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> rootInfo() {
        return Map.of(
            "service", "JML Access Reconciliation Engine - Backend API Service",
            "status", "UP",
            "port", 8080,
            "apiEndpoints", "/api/dashboard/summary",
            "h2Console", "/h2-console"
        );
    }
}
