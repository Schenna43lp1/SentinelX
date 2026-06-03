package com.sentinelx.server.dto;

import com.sentinelx.server.domain.entity.Node;
import java.time.LocalDateTime;

public class NodeDto {

    private Long id;
    private String name;
    private String hostname;
    private String ipAddress;
    private String os;
    private String agentVersion;
    private String tags;
    private String status;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;

    public static NodeDto from(Node node) {
        NodeDto dto = new NodeDto();
        dto.id = node.getId();
        dto.name = node.getName();
        dto.hostname = node.getHostname();
        dto.ipAddress = node.getIpAddress();
        dto.os = node.getOs();
        dto.agentVersion = node.getAgentVersion();
        dto.tags = node.getTags();
        dto.status = node.getStatus().name();
        dto.lastSeen = node.getLastSeen();
        dto.createdAt = node.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getHostname() { return hostname; }
    public String getIpAddress() { return ipAddress; }
    public String getOs() { return os; }
    public String getAgentVersion() { return agentVersion; }
    public String getTags() { return tags; }
    public String getStatus() { return status; }
    public LocalDateTime getLastSeen() { return lastSeen; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
