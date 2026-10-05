package com.jml.reconciliation.service;

import com.jml.reconciliation.dto.NotificationAlert;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private final List<NotificationAlert> notifications = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public NotificationService() {
        // Seed initial real-time alerts
        addNotification(new NotificationAlert(
                UUID.randomUUID().toString(),
                "Critical SLA 15m Breach Risk",
                "Orphaned Leaver Marcus Brody (AWS ADMIN) pending immediate remediation. 4 mins remaining.",
                "CRITICAL",
                "assessment",
                4L
        ));

        addNotification(new NotificationAlert(
                UUID.randomUUID().toString(),
                "Dual-Control Sign-off Required",
                "Alex Mercer (Role Mover) pending security officer approval for GitHub ADMIN revocation.",
                "WARNING",
                "approvals",
                1L
        ));

        addNotification(new NotificationAlert(
                UUID.randomUUID().toString(),
                "Data Feed Health Nominal",
                "HR, Directory, Entitlements, and Approvals feeds fully operational (Confidence 1.00).",
                "INFO",
                "sources",
                null
        ));
    }

    public List<NotificationAlert> getRecentNotifications() {
        return new ArrayList<>(notifications);
    }

    public NotificationAlert addNotification(NotificationAlert alert) {
        notifications.add(0, alert);
        if (notifications.size() > 50) {
            notifications.remove(notifications.size() - 1);
        }

        // Broadcast to SSE clients
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(alert));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);

        return alert;
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(3600000L); // 1 hour timeout
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected to JML Real-time Alert Stream"));
        } catch (Exception e) {
            emitters.remove(emitter);
        }

        return emitter;
    }
}
