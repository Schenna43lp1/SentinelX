package com.sentinelx.server.service;

import com.sentinelx.server.domain.entity.Alert;
import com.sentinelx.server.domain.entity.Metric;
import com.sentinelx.server.domain.entity.Node;
import com.sentinelx.server.repository.MetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MetricService {

    private final MetricRepository metricRepository;
    private final AlertService alertService;
    private final SettingService settingService;

    public MetricService(MetricRepository metricRepository,
                         AlertService alertService,
                         SettingService settingService) {
        this.metricRepository = metricRepository;
        this.alertService = alertService;
        this.settingService = settingService;
    }

    @Transactional
    public Metric record(Node node, Metric metric) {
        metric.setNode(node);
        Metric saved = metricRepository.save(metric);
        evaluateThresholds(node, metric);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Metric> findRecentForNode(Node node, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return metricRepository.findByNodeAndCreatedAtAfterOrderByCreatedAtAsc(node, since);
    }

    @Transactional(readOnly = true)
    public Optional<Metric> findLatestForNode(Node node) {
        return metricRepository.findTopByNodeOrderByCreatedAtDesc(node);
    }

    @Transactional(readOnly = true)
    public double getAverageCpu() {
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);
        return metricRepository.findAverageCpuSince(since).orElse(0.0);
    }

    @Transactional(readOnly = true)
    public double getAverageRam() {
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);
        return metricRepository.findAverageRamSince(since).orElse(0.0);
    }

    private void evaluateThresholds(Node node, Metric metric) {
        double cpuThreshold = settingService.getDoubleValue(SettingService.KEY_CPU_THRESHOLD, 90.0);
        double ramThreshold = settingService.getDoubleValue(SettingService.KEY_RAM_THRESHOLD, 90.0);

        if (metric.getCpuUsagePercent() >= cpuThreshold) {
            alertService.raiseAlert(
                node,
                Alert.AlertType.CPU_HIGH,
                Alert.Severity.WARNING,
                "High CPU on " + node.getName(),
                String.format("CPU usage is %.1f%% (threshold: %.1f%%)", metric.getCpuUsagePercent(), cpuThreshold)
            );
        }

        if (metric.getRamUsagePercent() >= ramThreshold) {
            alertService.raiseAlert(
                node,
                Alert.AlertType.RAM_HIGH,
                Alert.Severity.WARNING,
                "High RAM on " + node.getName(),
                String.format("RAM usage is %.1f%% (threshold: %.1f%%)", metric.getRamUsagePercent(), ramThreshold)
            );
        }
    }
}
