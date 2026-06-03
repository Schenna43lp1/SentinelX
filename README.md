# SentinelX v0.1

Self-hosted monitoring and alerting platform for homelabs and small teams.

## Features

- **Dashboard** — live overview of nodes, alerts, CPU & RAM
- **Node management** — add, edit, delete monitored hosts
- **Java Agent** — lightweight metrics collector using OSHI
- **Alert engine** — CPU/RAM thresholds, node offline detection
- **Telegram alerts** — real-time notifications on WARNING/CRITICAL
- **Role-based access** — ADMIN and VIEWER roles
- **Dark mode UI** — Bootstrap 5 throughout

## Default Credentials

| Field    | Value          |
|----------|----------------|
| Username | `admin`        |
| Password | `change-me-now`|

**Change the password immediately after first login.**

---

## Quick Start

### 1. Start PostgreSQL

```bash
cd docker
docker compose up -d
```

### 2. Build the project

```bash
mvn clean package -DskipTests
```

### 3. Run the server

```bash
java -jar sentinelx-server/target/sentinelx-server-0.1.0-SNAPSHOT.jar
```

The server starts on **http://localhost:8080**.

Environment variables you can override:

| Variable               | Default                                    | Description                    |
|------------------------|--------------------------------------------|--------------------------------|
| `DB_URL`               | `jdbc:postgresql://localhost:5432/sentinelx` | PostgreSQL JDBC URL           |
| `DB_USERNAME`          | `sentinelx`                                | Database username              |
| `DB_PASSWORD`          | `sentinelx`                                | Database password              |
| `SERVER_PORT`          | `8080`                                     | HTTP listen port               |
| `AGENT_TIMEOUT_SECONDS`| `60`                                       | Seconds before node goes offline |

### 4. Configure and run the agent

Copy `sentinel-agent/src/main/resources/agent.yml` next to the agent jar:

```yaml
server_url: http://localhost:8080
agent_token: ""          # Fill in after first run
interval_seconds: 30
node_name: my-node
```

Run the agent:

```bash
java -jar sentinel-agent/target/sentinel-agent-0.1.0-SNAPSHOT.jar
```

On first run with an empty `agent_token`, the agent registers itself and logs instructions. Copy the token from the SentinelX UI (**Nodes → your node → Agent Token**), paste it into `agent.yml`, and restart the agent.

---

## Project Structure

```
sentinelx/
├── sentinelx-server/          # Spring Boot server
│   ├── src/main/java/
│   │   └── com/sentinelx/server/
│   │       ├── controller/    # Web + REST controllers
│   │       ├── domain/entity/ # JPA entities
│   │       ├── dto/           # Request/response DTOs
│   │       ├── monitoring/    # Ping, HTTP, TCP, offline checks
│   │       ├── repository/    # Spring Data JPA
│   │       ├── security/      # Spring Security config + agent token filter
│   │       └── service/       # Business logic
│   └── src/main/resources/
│       ├── db/migration/      # Flyway SQL migrations
│       ├── static/            # CSS, JS
│       └── templates/         # Thymeleaf HTML
├── sentinel-agent/            # Standalone Java agent
│   └── src/main/java/
│       └── com/sentinelx/agent/
│           ├── config/        # YAML config loader
│           ├── metrics/       # OSHI collector
│           └── push/          # HTTP pusher
├── docker/
│   └── docker-compose.yml
├── docs/
│   └── api-contract.md
└── pom.xml
```

---

## API

See [docs/api-contract.md](docs/api-contract.md) for the full REST API reference.

Key endpoints:

| Method | Path                         | Description                  |
|--------|------------------------------|------------------------------|
| POST   | `/api/v1/agent/register`     | Register a node              |
| POST   | `/api/v1/agent/metrics`      | Push metrics (Bearer token)  |
| GET    | `/api/v1/nodes`              | List nodes                   |
| GET    | `/api/v1/alerts`             | List alerts                  |
| POST   | `/api/v1/alerts/{id}/ack`    | Acknowledge alert            |

---

## Telegram Setup

1. Create a bot with [@BotFather](https://t.me/BotFather) and copy the token.
2. Add the bot to a group, or start a direct conversation.
3. Get the chat ID (start the bot, then use the Telegram API `getUpdates`).
4. In SentinelX: **Settings → Telegram** — paste the token and chat ID.

Alerts at WARNING or CRITICAL severity are sent automatically.

---

## Screenshots

_Screenshots placeholder — run the server and visit http://localhost:8080_

---

## Tech Stack

- Java 21, Spring Boot 3.3, Maven
- PostgreSQL 16, Flyway migrations
- Spring Security (BCrypt, form login, session)
- Thymeleaf + Bootstrap 5 + Chart.js
- OSHI (system metrics in the agent)
- Java 11 HTTP Client

## Roadmap (v0.2+)

- SSL certificate expiry checks
- Uptime history graphs
- Multi-user management
- Email notifications
- Scheduled reports
