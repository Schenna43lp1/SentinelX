package com.sentinelx.agent.config;

public class AgentConfig {

    private String serverUrl;
    private String agentToken;
    private int intervalSeconds;
    private String nodeName;

    public String getServerUrl() { return serverUrl; }
    public void setServerUrl(String serverUrl) { this.serverUrl = serverUrl; }

    public String getAgentToken() { return agentToken; }
    public void setAgentToken(String agentToken) { this.agentToken = agentToken; }

    public int getIntervalSeconds() { return intervalSeconds > 0 ? intervalSeconds : 30; }
    public void setIntervalSeconds(int intervalSeconds) { this.intervalSeconds = intervalSeconds; }

    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
}
