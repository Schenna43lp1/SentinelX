# SentinelX v0.1 — Setup Guide

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose (for PostgreSQL)

## Step-by-step

### 1. Clone and build

```bash
git clone <repo-url>
cd sentinelx
mvn clean package -DskipTests
```

### 2. Start the database

```bash
cd docker
docker compose up -d
```

Verify it's running:

```bash
docker compose ps
```

### 3. Start the server

```bash
java -jar sentinelx-server/target/sentinelx-server-0.1.0-SNAPSHOT.jar
```

Or with custom DB settings:

```bash
DB_URL=jdbc:postgresql://myhost:5432/sentinelx \
DB_USERNAME=myuser \
DB_PASSWORD=mypass \
java -jar sentinelx-server/target/sentinelx-server-0.1.0-SNAPSHOT.jar
```

Flyway runs automatically and creates all tables + the default admin user on first start.

### 4. Login

Open http://localhost:8080 and log in with:

- Username: `admin`
- Password: `change-me-now`

**Change the password** — this is a seed-only credential stored with BCrypt. To change it:
1. (v0.1) Use a PostgreSQL client to update the hash:
   ```sql
   UPDATE users SET password_hash = '<new-bcrypt-hash>' WHERE username = 'admin';
   ```
   Generate a BCrypt hash online or with: `htpasswd -bnBC 12 "" newpassword | tr -d ':\n'`

### 5. Add a node

Go to **Nodes → Add Node** and fill in the details. Copy the generated agent token from the node detail page.

### 6. Configure and run the agent

```bash
# Copy the example config
cp sentinel-agent/src/main/resources/agent.yml ./agent.yml

# Edit it
nano agent.yml
# Set: server_url, agent_token, node_name

# Run
java -jar sentinel-agent/target/sentinel-agent-0.1.0-SNAPSHOT.jar
```

### 7. Configure Telegram (optional)

Go to **Settings** and fill in:
- Telegram Bot Token (from @BotFather)
- Telegram Chat ID

### 8. Set alert thresholds

In **Settings**, adjust CPU/RAM thresholds and agent timeout to match your environment.

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| Server won't start | Check DB_URL/credentials, ensure PostgreSQL is running |
| Agent gets 401 | Verify `agent_token` in `agent.yml` matches the UI |
| No metrics on dashboard | Agent must be running and the token must be valid |
| Telegram not sending | Verify bot token and chat ID; check server logs |
| Node stays OFFLINE | Reduce agent `interval_seconds` or increase `agent.timeout.seconds` in Settings |
