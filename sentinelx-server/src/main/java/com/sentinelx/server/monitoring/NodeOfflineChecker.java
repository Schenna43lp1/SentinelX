package com.sentinelx.server.monitoring;

import com.sentinelx.server.domain.entity.Alert;
import com.sentinelx.server.domain.entity.Node;
import com.sentinelx.server.repository.NodeRepository;
import com.sentinelx.server.service.AlertService;
import com.sentinelx.server.service.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Periodically marks nodes as OFFLINE when their last heartbeat exceeds the timeout threshold.
 */
@Component
public class NodeOfflineChecker {

    private static final Logger log = LoggerFactory.getLogger(NodeOfflineChecker.class);

    private final NodeRepository nodeRepository;
    private final AlertService alertService;
    private final SettingService settingService;

    @Value("${sentinelx.agent.timeout-seconds:60}")
    private long defaultTimeoutSeconds;

    public NodeOfflineChecker(NodeRepository nodeRepository,
                               AlertService alertService,
                               SettingService settingService) {
        this.nodeRepository = nodeRepository;
        this.alertService = alertService;
        this.settingService = settingService;
    }

    @Scheduled(fixedDelayString = "${sentinelx.agent.timeout-seconds:60}000")
    @Transactional
    public void checkNodeHeartbeats() {
        long timeoutSeconds = settingService.getLongValue(SettingService.KEY_AGENT_TIMEOUT_SECONDS, defaultTimeoutSeconds);
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(timeoutSeconds);

        List<Node> allNodes = nodeRepository.findAll();
        for (Node node : allNodes) {
            boolean timedOut = node.getLastSeen() == null || node.getLastSeen().isBefore(cutoff);

            if (timedOut && node.getStatus() == Node.NodeStatus.ONLINE) {
                node.setStatus(Node.NodeStatus.OFFLINE);
                nodeRepository.save(node);
                log.warn("Node {} ({}) marked OFFLINE - last seen: {}", node.getName(), node.getId(), node.getLastSeen());

                alertService.raiseAlert(
                    node,
                    Alert.AlertType.NODE_OFFLINE,
                    Alert.Severity.CRITICAL,
                    "Node offline: " + node.getName(),
                    String.format("Node '%s' (hostname: %s) has not reported in over %d seconds.",
                        node.getName(), node.getHostname(), timeoutSeconds)
                );
            }
        }
    }
}
