package com.sentinelx.server.controller.api;

import com.sentinelx.server.domain.entity.Metric;
import com.sentinelx.server.domain.entity.Node;
import com.sentinelx.server.dto.AgentRegistrationRequest;
import com.sentinelx.server.dto.AgentRegistrationResponse;
import com.sentinelx.server.dto.MetricPayload;
import com.sentinelx.server.service.MetricService;
import com.sentinelx.server.service.NodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentApiController {

    private final NodeService nodeService;
    private final MetricService metricService;

    public AgentApiController(NodeService nodeService, MetricService metricService) {
        this.nodeService = nodeService;
        this.metricService = metricService;
    }

    /**
     * Called by a new agent installation to obtain a token.
     * Returns existing node if hostname already registered.
     */
    @PostMapping("/register")
    public ResponseEntity<AgentRegistrationResponse> register(
            @Valid @RequestBody AgentRegistrationRequest request) {

        // Check if a node with this hostname already exists
        Node node = nodeService.findAll().stream()
            .filter(n -> n.getHostname().equalsIgnoreCase(request.getHostname()))
            .findFirst()
            .map(existing -> nodeService.heartbeat(existing.getId(), request.getAgentVersion(), request.getOs()))
            .orElseGet(() -> {
                Node newNode = new Node();
                newNode.setName(request.getNodeName());
                newNode.setHostname(request.getHostname());
                newNode.setIpAddress(request.getIpAddress());
                newNode.setOs(request.getOs());
                newNode.setAgentVersion(request.getAgentVersion());
                Node created = nodeService.create(newNode);
                // Immediately mark ONLINE so the node doesn't appear stale on first view
                return nodeService.heartbeat(created.getId(), request.getAgentVersion(), request.getOs());
            });

        return ResponseEntity.status(HttpStatus.OK).body(
            new AgentRegistrationResponse(node.getId(), node.getAgentToken(), "Node registered")
        );
    }

    /**
     * Receives a metric push from an authenticated agent.
     * The node is identified by the Bearer token validated in AgentTokenFilter.
     */
    @PostMapping("/metrics")
    public ResponseEntity<Map<String, String>> receiveMetrics(
            @Valid @RequestBody MetricPayload payload,
            HttpServletRequest request) {

        Long nodeId = (Long) request.getAttribute("authenticatedNodeId");

        // Update heartbeat and optional metadata
        Node node = nodeService.heartbeat(nodeId, payload.getAgentVersion(), payload.getOs());

        Metric metric = new Metric();
        metric.setCpuUsagePercent(payload.getCpuUsagePercent());
        metric.setRamUsagePercent(payload.getRamUsagePercent());
        metric.setDiskUsagePercent(payload.getDiskUsagePercent());
        metric.setUptimeSeconds(payload.getUptimeSeconds());

        metricService.record(node, metric);

        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
