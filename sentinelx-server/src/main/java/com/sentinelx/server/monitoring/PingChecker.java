package com.sentinelx.server.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * ICMP ping check. Falls back to TCP port 7 if ICMP is not permitted.
 */
@Component
public class PingChecker {

    private static final Logger log = LoggerFactory.getLogger(PingChecker.class);

    public boolean isReachable(String host, int timeoutMs) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isReachable(timeoutMs);
        } catch (Exception e) {
            log.debug("Ping check failed for {}: {}", host, e.getMessage());
            return false;
        }
    }
}
