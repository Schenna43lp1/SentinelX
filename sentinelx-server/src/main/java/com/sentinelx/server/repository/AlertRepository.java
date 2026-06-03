package com.sentinelx.server.repository;

import com.sentinelx.server.domain.entity.Alert;
import com.sentinelx.server.domain.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByStatusOrderByCreatedAtDesc(Alert.AlertStatus status);

    List<Alert> findByNodeAndStatusOrderByCreatedAtDesc(Node node, Alert.AlertStatus status);

    List<Alert> findAllByOrderByCreatedAtDesc();

    long countByStatus(Alert.AlertStatus status);

    boolean existsByNodeAndTypeAndStatus(Node node, Alert.AlertType type, Alert.AlertStatus status);
}
