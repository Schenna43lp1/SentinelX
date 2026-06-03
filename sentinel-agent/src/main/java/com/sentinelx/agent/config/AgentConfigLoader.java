package com.sentinelx.agent.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class AgentConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(AgentConfigLoader.class);
    private static final String CONFIG_FILE = "agent.yml";

    @SuppressWarnings("unchecked")
    public static AgentConfig load() {
        Yaml yaml = new Yaml();
        Map<String, Object> raw;

        try (InputStream in = openConfigStream()) {
            raw = yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE + ": " + e.getMessage(), e);
        }

        AgentConfig config = new AgentConfig();

        config.setServerUrl(getString(raw, "server_url", "http://localhost:8080"));
        config.setAgentToken(getString(raw, "agent_token", ""));
        config.setNodeName(getString(raw, "node_name", "unknown-node"));
        config.setIntervalSeconds(getInt(raw, "interval_seconds", 30));

        if (config.getAgentToken().isBlank()) {
            log.warn("agent_token is not set in {}. " +
                "The agent will attempt registration but cannot push metrics until a token is configured.", CONFIG_FILE);
        }

        return config;
    }

    private static InputStream openConfigStream() throws Exception {
        // 1. Try file alongside the jar
        try {
            return new FileInputStream(CONFIG_FILE);
        } catch (Exception e) {
            // 2. Fall back to classpath (for dev/IDE runs)
            InputStream cp = AgentConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (cp != null) {
                log.info("Using classpath {}", CONFIG_FILE);
                return cp;
            }
        }
        throw new RuntimeException("Cannot find " + CONFIG_FILE + " on filesystem or classpath");
    }

    private static String getString(Map<String, Object> map, String key, String defaultValue) {
        Object v = map.get(key);
        return v != null ? v.toString() : defaultValue;
    }

    private static int getInt(Map<String, Object> map, String key, int defaultValue) {
        Object v = map.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }
}
