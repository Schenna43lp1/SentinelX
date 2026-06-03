package com.sentinelx.server.dto;

import com.sentinelx.server.domain.entity.Alert;
import java.time.LocalDateTime;

public class AlertDto {

    private Long id;
    private String nodeName;
    private Long nodeId;
    private String title;
    private String message;
    private String status;
    private String severity;
    private String type;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    private LocalDateTime createdAt;

    public static AlertDto from(Alert alert) {
        AlertDto dto = new AlertDto();
        dto.id = alert.getId();
        dto.title = alert.getTitle();
        dto.message = alert.getMessage();
        dto.status = alert.getStatus().name();
        dto.severity = alert.getSeverity().name();
        dto.type = alert.getType().name();
        dto.acknowledgedAt = alert.getAcknowledgedAt();
        dto.acknowledgedBy = alert.getAcknowledgedBy();
        dto.createdAt = alert.getCreatedAt();
        if (alert.getNode() != null) {
            dto.nodeId = alert.getNode().getId();
            dto.nodeName = alert.getNode().getName();
        }
        return dto;
    }

    public Long getId() { return id; }
    public String getNodeName() { return nodeName; }
    public Long getNodeId() { return nodeId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getSeverity() { return severity; }
    public String getType() { return type; }
    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public String getAcknowledgedBy() { return acknowledgedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
