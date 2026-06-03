package com.sentinelx.server.repository;

import com.sentinelx.server.domain.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Optional<Node> findByAgentToken(String agentToken);
    List<Node> findByStatus(Node.NodeStatus status);
    long countByStatus(Node.NodeStatus status);
    boolean existsByAgentToken(String agentToken);
}
