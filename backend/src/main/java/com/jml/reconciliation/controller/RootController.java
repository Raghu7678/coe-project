package com.jml.reconciliation.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Root Health & Diagnostic Controller.
 * 
 * Provides an unauthenticated liveness and system metadata endpoint (/)
 * used by Docker health checks, reverse proxies, and integration smoke tests.
 */
@RestController
@CrossOrigin(originPatterns = "*")
public class RootController {

    /**
     * Returns service health, version, and documentation pointers.
     * 
     * @return Map containing service status, version, and H2 database console path
     */
    @GetMapping("/")
    public Map<String, String> getRootStatus() {
        return Map.of(
                "service", "JML Access Reconciliation & Risk Pre-Qualification Engine API",
                "status", "UP",
                "documentation", "/h2-console",
                "version", "1.0.0"
        );
    }
}
