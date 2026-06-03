-- SentinelX v0.1 Initial Schema

CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE nodes (
    id            BIGSERIAL    PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    hostname      VARCHAR(255) NOT NULL,
    ip_address    VARCHAR(45),
    os            VARCHAR(100),
    agent_version VARCHAR(50),
    tags          VARCHAR(500),
    agent_token   VARCHAR(255) NOT NULL UNIQUE,
    status        VARCHAR(20)  NOT NULL DEFAULT 'OFFLINE',
    last_seen     TIMESTAMP,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_nodes_status ON nodes(status);
CREATE INDEX idx_nodes_agent_token ON nodes(agent_token);

CREATE TABLE metrics (
    id                 BIGSERIAL   PRIMARY KEY,
    node_id            BIGINT      NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    cpu_usage_percent  DOUBLE PRECISION NOT NULL,
    ram_usage_percent  DOUBLE PRECISION NOT NULL,
    disk_usage_percent DOUBLE PRECISION NOT NULL,
    uptime_seconds     BIGINT      NOT NULL DEFAULT 0,
    created_at         TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_metrics_node_id    ON metrics(node_id);
CREATE INDEX idx_metrics_created_at ON metrics(created_at);

CREATE TABLE alerts (
    id              BIGSERIAL    PRIMARY KEY,
    node_id         BIGINT       REFERENCES nodes(id) ON DELETE SET NULL,
    title           VARCHAR(255) NOT NULL,
    message         VARCHAR(1000) NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    severity        VARCHAR(20)  NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    acknowledged_at TIMESTAMP,
    acknowledged_by VARCHAR(255),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_alerts_node_id    ON alerts(node_id);
CREATE INDEX idx_alerts_status     ON alerts(status);
CREATE INDEX idx_alerts_severity   ON alerts(severity);
CREATE INDEX idx_alerts_created_at ON alerts(created_at);

CREATE TABLE settings (
    id          BIGSERIAL     PRIMARY KEY,
    key         VARCHAR(100)  NOT NULL UNIQUE,
    value       VARCHAR(1000),
    description VARCHAR(500),
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);
