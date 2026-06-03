package com.sentinelx.server.controller.api;

import com.sentinelx.server.dto.AlertDto;
import com.sentinelx.server.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertApiController {

    private final AlertService alertService;

    public AlertApiController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<List<AlertDto>> listAlerts() {
        List<AlertDto> alerts = alertService.findAll().stream()
            .map(AlertDto::from)
            .toList();
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/{id}/ack")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertDto> acknowledge(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(AlertDto.from(alertService.acknowledge(id, auth.getName())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
