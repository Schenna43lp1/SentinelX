package com.sentinelx.server.dto;

public class AgentRegistrationResponse {

    private Long nodeId;
    private String agentToken;
    private String message;

    public AgentRegistrationResponse(Long nodeId, String agentToken, String message) {
        this.nodeId = nodeId;
        this.agentToken = agentToken;
        this.message = message;
    }

    public Long getNodeId() { return nodeId; }
    public String getAgentToken() { return agentToken; }
    public String getMessage() { return message; }
}
