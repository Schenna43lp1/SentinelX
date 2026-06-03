package com.sentinelx.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AgentRegistrationRequest {

    @NotBlank
    @Size(max = 255)
    private String nodeName;

    @NotBlank
    @Size(max = 255)
    private String hostname;

    @Size(max = 45)
    private String ipAddress;

    @Size(max = 100)
    private String os;

    @Size(max = 50)
    private String agentVersion;

    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public String getAgentVersion() { return agentVersion; }
    public void setAgentVersion(String agentVersion) { this.agentVersion = agentVersion; }
}
