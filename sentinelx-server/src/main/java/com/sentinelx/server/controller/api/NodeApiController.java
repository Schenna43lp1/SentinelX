package com.sentinelx.server.controller.api;

import com.sentinelx.server.dto.NodeDto;
import com.sentinelx.server.service.NodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes")
public class NodeApiController {

    private final NodeService nodeService;

    public NodeApiController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<List<NodeDto>> listNodes() {
        List<NodeDto> nodes = nodeService.findAll().stream()
            .map(NodeDto::from)
            .toList();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<NodeDto> getNode(@PathVariable("id") Long id) {
        return nodeService.findById(id)
            .map(NodeDto::from)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
