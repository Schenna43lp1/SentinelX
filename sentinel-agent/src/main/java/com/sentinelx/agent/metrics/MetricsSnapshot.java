package com.sentinelx.agent.metrics;

public class MetricsSnapshot {

    private String hostname;
    private String os;
    private double cpuUsagePercent;
    private double ramUsagePercent;
    private double diskUsagePercent;
    private long uptimeSeconds;
    private String agentVersion = "0.1.0";

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public double getCpuUsagePercent() { return cpuUsagePercent; }
    public void setCpuUsagePercent(double cpuUsagePercent) { this.cpuUsagePercent = cpuUsagePercent; }

    public double getRamUsagePercent() { return ramUsagePercent; }
    public void setRamUsagePercent(double ramUsagePercent) { this.ramUsagePercent = ramUsagePercent; }

    public double getDiskUsagePercent() { return diskUsagePercent; }
    public void setDiskUsagePercent(double diskUsagePercent) { this.diskUsagePercent = diskUsagePercent; }

    public long getUptimeSeconds() { return uptimeSeconds; }
    public void setUptimeSeconds(long uptimeSeconds) { this.uptimeSeconds = uptimeSeconds; }

    public String getAgentVersion() { return agentVersion; }
    public void setAgentVersion(String agentVersion) { this.agentVersion = agentVersion; }
}
