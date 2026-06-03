package com.sentinelx.server.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * TCP port reachability check.
 */
@Component
public class TcpChecker {

    private static final Logger log = LoggerFactory.getLogger(TcpChecker.class);

    public boolean isPortOpen(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            log.debug("TCP check failed for {}:{}: {}", host, port, e.getMessage());
            return false;
        }
    }
}
