package com.jml.reconciliation.controller;

import com.jml.reconciliation.dto.NotificationAlert;
import com.jml.reconciliation.service.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * REST & SSE Controller for Real-time Notification Alerts & Telemetry.
 * 
 * Provides:
 * - Polled notification list for recent security alerts, SLA breach warnings, and degraded feeds.
 * - Server-Sent Events (SSE) streaming endpoint for live pushes directly to the browser navbar badge.
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(originPatterns = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Retrieves recent notification alerts in descending order of occurrence.
     * 
     * @return List of NotificationAlert items
     */
    @GetMapping
    public List<NotificationAlert> getNotifications() {
        return notificationService.getRecentNotifications();
    }

    /**
     * Subscribes the client browser to real-time Server-Sent Events (SSE) alert stream.
     * Pushes notifications immediately when high-risk anomalies or safety gate triggers occur.
     * 
     * @return SseEmitter streaming event pipeline
     */
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications() {
        return notificationService.subscribe();
    }
}
