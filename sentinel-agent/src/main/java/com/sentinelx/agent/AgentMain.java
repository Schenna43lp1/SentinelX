package com.sentinelx.agent;

import com.sentinelx.agent.config.AgentConfig;
import com.sentinelx.agent.config.AgentConfigLoader;
import com.sentinelx.agent.metrics.MetricsCollector;
import com.sentinelx.agent.push.MetricsPusher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AgentMain {

    private static final Logger log = LoggerFactory.getLogger(AgentMain.class);

    public static void main(String[] args) {
        log.info("SentinelX Agent v0.1 starting...");

        AgentConfig config = AgentConfigLoader.load();
        log.info("Connecting to server: {}", config.getServerUrl());
        log.info("Node name: {}", config.getNodeName());
        log.info("Interval: {}s", config.getIntervalSeconds());

        MetricsCollector collector = new MetricsCollector();
        MetricsPusher pusher = new MetricsPusher(config);

        // Register node on startup (idempotent — server returns existing token if hostname matches)
        try {
            pusher.register(collector.collectRegistration());
        } catch (Exception e) {
            log.error("Failed to register with server: {}. Will retry on next interval.", e.getMessage());
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sentinelx-agent");
            t.setDaemon(false);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                pusher.push(collector.collect());
            } catch (Exception e) {
                log.error("Failed to push metrics: {}", e.getMessage());
            }
        }, 0, config.getIntervalSeconds(), TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("SentinelX Agent shutting down...");
            scheduler.shutdownNow();
        }));

        log.info("Agent running. Press Ctrl+C to stop.");
    }
}
