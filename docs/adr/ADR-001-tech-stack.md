# ADR-001: Technology Stack for SentinelX v0.1

**Status:** Accepted  
**Date:** 2024-01-01

## Context

We needed a self-hosted monitoring platform suitable for homelabs and small teams with a 2-person team and a tight v0.1 scope.

## Decisions

| Concern | Decision | Reason |
|---------|----------|--------|
| Language | Java 21 | Team expertise; virtual threads available for future use |
| Framework | Spring Boot 3.3 | Mature ecosystem; Security, JPA, Scheduling included |
| Build | Maven multi-module | Clean separation of server vs. agent |
| Database | PostgreSQL | Reliable; Flyway migration support |
| ORM | Spring Data JPA / Hibernate | Standard; avoids raw SQL for simple queries |
| Migrations | Flyway | Deterministic schema evolution |
| UI | Thymeleaf + Bootstrap 5 | Server-rendered; no SPA complexity for v0.1 |
| Charts | Chart.js | Lightweight; CDN-deliverable |
| Agent metrics | OSHI | Cross-platform Java system metrics library |
| Agent config | SnakeYAML | Simple YAML file; no Spring dependency in agent |
| Notifications | Telegram Bot API | Easy to set up; free; reliable push delivery |

## Rejected Alternatives

- **Kotlin** — team prefers Java for now
- **GraphQL** — overkill for v0.1; REST is sufficient
- **WebSockets** — deferred to v0.2; polling is adequate at 30s intervals
- **MongoDB** — time-series data fits PostgreSQL well enough at homelab scale
- **Kafka** — unnecessary complexity for 2 devs and small deployments
