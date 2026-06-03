package com.sentinelx.server.controller.api;

import com.sentinelx.server.domain.entity.Metric;
import com.sentinelx.server.domain.entity.Node;
import com.sentinelx.server.service.MetricService;
import com.sentinelx.server.service.NodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/nodes/{nodeId}/metrics")
public class MetricApiController {

    private final NodeService nodeService;
    private final MetricService metricService;

    public MetricApiController(NodeService nodeService, MetricService metricService) {
        this.nodeService = nodeService;
        this.metricService = metricService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<List<Map<String, Object>>> getMetrics(
            @PathVariable("nodeId") Long nodeId,
            @RequestParam(value = "hours", defaultValue = "1") int hours) {

        Node node = nodeService.findById(nodeId)
            .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));

        List<Map<String, Object>> result = metricService.findRecentForNode(node, hours).stream()
            .map(m -> Map.<String, Object>of(
                "createdAt", m.getCreatedAt().toString(),
                "cpuUsagePercent", m.getCpuUsagePercent(),
                "ramUsagePercent", m.getRamUsagePercent(),
                "diskUsagePercent", m.getDiskUsagePercent(),
                "uptimeSeconds", m.getUptimeSeconds()
            ))
            .toList();

        return ResponseEntity.ok(result);
    }
}
