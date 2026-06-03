package com.sentinelx.server.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MetricPayload {

    @NotBlank
    @Size(max = 255)
    private String hostname;

    @Size(max = 100)
    private String os;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double cpuUsagePercent;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double ramUsagePercent;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double diskUsagePercent;

    private long uptimeSeconds;

    @Size(max = 50)
    private String agentVersion;

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
