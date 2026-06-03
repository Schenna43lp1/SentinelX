package com.sentinelx.server.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Performs HTTP/HTTPS reachability checks against a URL.
 * Returns true if the server responds with a 2xx or 3xx status.
 */
@Component
public class HttpChecker {

    private static final Logger log = LoggerFactory.getLogger(HttpChecker.class);

    private final HttpClient httpClient;

    public HttpChecker() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    public CheckResult check(String url, int timeoutSeconds) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .header("User-Agent", "SentinelX-Monitor/0.1")
                .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            int status = response.statusCode();
            boolean up = status >= 200 && status < 400;
            return new CheckResult(up, status, null);

        } catch (Exception e) {
            log.debug("HTTP check failed for {}: {}", url, e.getMessage());
            return new CheckResult(false, -1, e.getMessage());
        }
    }

    public record CheckResult(boolean up, int statusCode, String errorMessage) {}
}
