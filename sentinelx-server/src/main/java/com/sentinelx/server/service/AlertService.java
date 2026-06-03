package com.sentinelx.server.service;

import com.sentinelx.server.domain.entity.Alert;
import com.sentinelx.server.domain.entity.Node;
import com.sentinelx.server.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;
    private final TelegramNotificationService telegramService;

    public AlertService(AlertRepository alertRepository, TelegramNotificationService telegramService) {
        this.alertRepository = alertRepository;
        this.telegramService = telegramService;
    }

    @Transactional(readOnly = true)
    public List<Alert> findAll() {
        return alertRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Alert> findOpen() {
        return alertRepository.findByStatusOrderByCreatedAtDesc(Alert.AlertStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public long countOpen() {
        return alertRepository.countByStatus(Alert.AlertStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public Optional<Alert> findById(Long id) {
        return alertRepository.findById(id);
    }

    /**
     * Creates an alert only if one of the same type is not already open for the node.
     * Sends Telegram notification for WARNING and CRITICAL severity.
     */
    @Transactional
    public void raiseAlert(Node node, Alert.AlertType type, Alert.Severity severity, String title, String message) {
        boolean alreadyOpen = alertRepository.existsByNodeAndTypeAndStatus(node, type, Alert.AlertStatus.OPEN);
        if (alreadyOpen) {
            return;
        }

        Alert alert = new Alert();
        alert.setNode(node);
        alert.setType(type);
        alert.setSeverity(severity);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setStatus(Alert.AlertStatus.OPEN);
        alertRepository.save(alert);

        log.warn("Alert raised [{}] {} - {}", severity, title, message);

        if (severity == Alert.Severity.WARNING || severity == Alert.Severity.CRITICAL) {
            telegramService.sendAlert(title, message);
        }
    }

    @Transactional
    public Alert acknowledge(Long id, String acknowledgedBy) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + id));

        alert.setStatus(Alert.AlertStatus.ACKNOWLEDGED);
        alert.setAcknowledgedAt(LocalDateTime.now());
        alert.setAcknowledgedBy(acknowledgedBy);
        return alertRepository.save(alert);
    }
}
