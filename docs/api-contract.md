# SentinelX v0.1 — API Contract

Base URL: `http://localhost:8080`

All agent endpoints use Bearer token authentication:
```
Authorization: Bearer <agent_token>
```

All user-facing API endpoints require session authentication (login first).

---

## Agent Endpoints

### POST /api/v1/agent/register

Register a new node or retrieve credentials for an existing one.

**Auth:** None

**Request body:**
```json
{
  "nodeName": "my-server",
  "hostname": "server.local",
  "ipAddress": "192.168.1.100",
  "os": "Ubuntu 24.04",
  "agentVersion": "0.1.0"
}
```

**Response 200:**
```json
{
  "nodeId": 1,
  "agentToken": "abc123...",
  "message": "Node registered"
}
```

---

### POST /api/v1/agent/metrics

Push a metrics snapshot. Called on each interval by the agent.

**Auth:** `Authorization: Bearer <agent_token>`

**Request body:**
```json
{
  "hostname": "server.local",
  "os": "Ubuntu 24.04",
  "cpuUsagePercent": 42.5,
  "ramUsagePercent": 68.1,
  "diskUsagePercent": 55.0,
  "uptimeSeconds": 86400,
  "agentVersion": "0.1.0"
}
```

**Response 200:**
```json
{ "status": "ok" }
```

---

## Node Endpoints

### GET /api/v1/nodes

List all nodes.

**Auth:** Session (ADMIN or VIEWER)

**Response 200:**
```json
[
  {
    "id": 1,
    "name": "My Server",
    "hostname": "server.local",
    "ipAddress": "192.168.1.100",
    "os": "Ubuntu 24.04",
    "agentVersion": "0.1.0",
    "tags": "homelab,linux",
    "status": "ONLINE",
    "lastSeen": "2024-01-01T12:00:00",
    "createdAt": "2024-01-01T10:00:00"
  }
]
```

---

### GET /api/v1/nodes/{id}

Get a single node by ID.

**Auth:** Session (ADMIN or VIEWER)

---

### GET /api/v1/nodes/{nodeId}/metrics?hours=1

Get metric history for a node (default: last 1 hour).

**Auth:** Session (ADMIN or VIEWER)

**Response 200:**
```json
[
  {
    "createdAt": "2024-01-01T12:00:00",
    "cpuUsagePercent": 42.5,
    "ramUsagePercent": 68.1,
    "diskUsagePercent": 55.0,
    "uptimeSeconds": 86400
  }
]
```

---

## Alert Endpoints

### GET /api/v1/alerts

List all alerts (newest first).

**Auth:** Session (ADMIN or VIEWER)

**Response 200:**
```json
[
  {
    "id": 1,
    "nodeId": 1,
    "nodeName": "My Server",
    "title": "Node offline: My Server",
    "message": "Node 'My Server' has not reported in over 60 seconds.",
    "status": "OPEN",
    "severity": "CRITICAL",
    "type": "NODE_OFFLINE",
    "acknowledgedAt": null,
    "acknowledgedBy": null,
    "createdAt": "2024-01-01T12:05:00"
  }
]
```

---

### POST /api/v1/alerts/{id}/ack

Acknowledge an open alert.

**Auth:** Session (ADMIN only)

**Response 200:** Updated `AlertDto`

---

## Severity Levels

| Severity | Used for |
|----------|----------|
| INFO     | Informational, no action needed |
| WARNING  | CPU/RAM threshold breached |
| CRITICAL | Node offline, HTTP down |

## Alert Types

| Type        | Trigger |
|-------------|---------|
| NODE_OFFLINE | Agent heartbeat timeout |
| CPU_HIGH    | CPU exceeds threshold |
| RAM_HIGH    | RAM exceeds threshold |
| HTTP_DOWN   | HTTP check returned non-2xx or timed out |
| PING_FAIL   | ICMP ping failed |
| TCP_FAIL    | TCP port unreachable |
