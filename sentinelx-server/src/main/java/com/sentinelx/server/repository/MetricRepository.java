package com.sentinelx.server.repository;

import com.sentinelx.server.domain.entity.Metric;
import com.sentinelx.server.domain.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {

    List<Metric> findByNodeOrderByCreatedAtDesc(Node node);

    List<Metric> findByNodeAndCreatedAtAfterOrderByCreatedAtAsc(Node node, LocalDateTime after);

    Optional<Metric> findTopByNodeOrderByCreatedAtDesc(Node node);

    @Query("SELECT AVG(m.cpuUsagePercent) FROM Metric m WHERE m.createdAt > :since")
    Optional<Double> findAverageCpuSince(@Param("since") LocalDateTime since);

    @Query("SELECT AVG(m.ramUsagePercent) FROM Metric m WHERE m.createdAt > :since")
    Optional<Double> findAverageRamSince(@Param("since") LocalDateTime since);

    void deleteByCreatedAtBefore(LocalDateTime before);
}
