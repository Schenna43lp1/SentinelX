package com.sentinelx.server.controller.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class DownloadController {

    private static final String AGENT_JAR_PATH = "downloads/sentinel-agent.jar";
    private static final String AGENT_FILENAME  = "sentinel-agent.jar";

    @GetMapping("/download/agent")
    public ResponseEntity<Resource> downloadAgent() throws IOException {
        ClassPathResource resource = new ClassPathResource(AGENT_JAR_PATH);

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + AGENT_FILENAME + "\"")
                .body(resource);
    }

    /** Quick availability check used by the dashboard UI */
    @GetMapping("/download/agent/available")
    public ResponseEntity<Void> agentAvailable() {
        boolean exists = new ClassPathResource(AGENT_JAR_PATH).exists();
        return exists
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
