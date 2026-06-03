package com.sentinelx.server.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "metrics", indexes = {
    @Index(name = "idx_metrics_node_id", columnList = "node_id"),
    @Index(name = "idx_metrics_created_at", columnList = "created_at")
})
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "node_id", nullable = false)
    private Node node;

    @Column(nullable = false)
    private double cpuUsagePercent;

    @Column(nullable = false)
    private double ramUsagePercent;

    @Column(nullable = false)
    private double diskUsagePercent;

    @Column
    private long uptimeSeconds;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Node getNode() { return node; }
    public void setNode(Node node) { this.node = node; }

    public double getCpuUsagePercent() { return cpuUsagePercent; }
    public void setCpuUsagePercent(double cpuUsagePercent) { this.cpuUsagePercent = cpuUsagePercent; }

    public double getRamUsagePercent() { return ramUsagePercent; }
    public void setRamUsagePercent(double ramUsagePercent) { this.ramUsagePercent = ramUsagePercent; }

    public double getDiskUsagePercent() { return diskUsagePercent; }
    public void setDiskUsagePercent(double diskUsagePercent) { this.diskUsagePercent = diskUsagePercent; }

    public long getUptimeSeconds() { return uptimeSeconds; }
    public void setUptimeSeconds(long uptimeSeconds) { this.uptimeSeconds = uptimeSeconds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
