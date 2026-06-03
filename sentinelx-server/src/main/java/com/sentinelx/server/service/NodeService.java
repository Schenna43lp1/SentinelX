package com.sentinelx.server.service;

import com.sentinelx.server.domain.entity.Node;
import com.sentinelx.server.repository.NodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NodeService {

    private final NodeRepository nodeRepository;

    public NodeService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Transactional(readOnly = true)
    public List<Node> findAll() {
        return nodeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Node> findById(Long id) {
        return nodeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Node> findByToken(String token) {
        return nodeRepository.findByAgentToken(token);
    }

    @Transactional(readOnly = true)
    public long countOnline() {
        return nodeRepository.countByStatus(Node.NodeStatus.ONLINE);
    }

    @Transactional(readOnly = true)
    public long countOffline() {
        return nodeRepository.countByStatus(Node.NodeStatus.OFFLINE);
    }

    @Transactional
    public Node create(Node node) {
        node.setAgentToken(generateToken());
        node.setStatus(Node.NodeStatus.OFFLINE);
        return nodeRepository.save(node);
    }

    @Transactional
    public Node update(Long id, Node updated) {
        Node existing = nodeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Node not found: " + id));

        existing.setName(updated.getName());
        existing.setHostname(updated.getHostname());
        existing.setIpAddress(updated.getIpAddress());
        existing.setOs(updated.getOs());
        existing.setTags(updated.getTags());
        // Preserve agent-reported fields if present
        if (updated.getAgentVersion() != null) {
            existing.setAgentVersion(updated.getAgentVersion());
        }
        if (updated.getLastSeen() != null) {
            existing.setLastSeen(updated.getLastSeen());
        }
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        return nodeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!nodeRepository.existsById(id)) {
            throw new IllegalArgumentException("Node not found: " + id);
        }
        nodeRepository.deleteById(id);
    }

    /** Updates heartbeat fields only — does not touch user-editable fields. */
    @Transactional
    public Node heartbeat(Long id, String agentVersion, String os) {
        Node node = nodeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Node not found: " + id));
        node.setLastSeen(java.time.LocalDateTime.now());
        node.setStatus(Node.NodeStatus.ONLINE);
        if (agentVersion != null && !agentVersion.isBlank()) {
            node.setAgentVersion(agentVersion);
        }
        if (os != null && !os.isBlank() && node.getOs() == null) {
            node.setOs(os);
        }
        return nodeRepository.save(node);
    }

    @Transactional
    public Node regenerateToken(Long id) {
        Node node = nodeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Node not found: " + id));
        node.setAgentToken(generateToken());
        return nodeRepository.save(node);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }
}
