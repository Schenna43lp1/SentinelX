package com.sentinelx.agent.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.sentinelx.agent.config.AgentConfig;
import com.sentinelx.agent.metrics.MetricsSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class MetricsPusher {

    private static final Logger log = LoggerFactory.getLogger(MetricsPusher.class);

    private final AgentConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MetricsPusher(AgentConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public void register(MetricsSnapshot snap) throws Exception {
        Map<String, Object> body = Map.of(
            "nodeName", config.getNodeName(),
            "hostname", snap.getHostname(),
            "os", snap.getOs() != null ? snap.getOs() : "",
            "agentVersion", snap.getAgentVersion()
        );

        String json = objectMapper.writeValueAsString(body);
        String url = config.getServerUrl().stripTrailing() + "/api/v1/agent/register";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .timeout(Duration.ofSeconds(15))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            try {
                var root = objectMapper.readTree(response.body());
                String returnedToken = root.path("agentToken").asText("");
                if (!returnedToken.isBlank()) {
                    config.setAgentToken(returnedToken);
                    log.info("Registered successfully. Node ID: {}", root.path("nodeId").asLong());
                } else {
                    log.warn("Registration response contained no agentToken — metrics push will be skipped.");
                }
            } catch (Exception e) {
                log.error("Failed to parse registration response: {}", e.getMessage());
            }
        } else {
            log.error("Registration failed: HTTP {} — {}", response.statusCode(), response.body());
        }
    }

    public void push(MetricsSnapshot snap) throws Exception {
        if (config.getAgentToken().isBlank()) {
            log.warn("Skipping metric push — no token yet. Waiting for registration to complete.");
            return;
        }

        Map<String, Object> body = Map.of(
            "hostname", snap.getHostname(),
            "os", snap.getOs() != null ? snap.getOs() : "",
            "cpuUsagePercent", snap.getCpuUsagePercent(),
            "ramUsagePercent", snap.getRamUsagePercent(),
            "diskUsagePercent", snap.getDiskUsagePercent(),
            "uptimeSeconds", snap.getUptimeSeconds(),
            "agentVersion", snap.getAgentVersion()
        );

        String json = objectMapper.writeValueAsString(body);
        String url = config.getServerUrl().stripTrailing() + "/api/v1/agent/metrics";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + config.getAgentToken())
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .timeout(Duration.ofSeconds(15))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            log.info("Metrics pushed: CPU={}% RAM={}%",
                String.format("%.1f", snap.getCpuUsagePercent()),
                String.format("%.1f", snap.getRamUsagePercent()));
        } else {
            log.error("Metric push failed: HTTP {} — {}", response.statusCode(), response.body());
        }
    }
}
