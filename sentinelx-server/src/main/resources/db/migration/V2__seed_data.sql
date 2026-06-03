-- Seed default roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('VIEWER')
ON CONFLICT (name) DO NOTHING;

-- Seed admin user (password: change-me-now, BCrypt encoded)
INSERT INTO users (username, password_hash, email, enabled, created_at)
VALUES (
    'admin',
    '$2a$10$CFwGPDvAp7OTrVgcc.EMSOdkP1bkf5yfkUXfucGPoynICZE6yGxzO',
    'admin@sentinelx.local',
    TRUE,
    NOW()
) ON CONFLICT (username) DO NOTHING;

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Seed default settings
INSERT INTO settings (key, value, description) VALUES
    ('telegram.bot.token',     '',   'Telegram Bot API token for alert notifications'),
    ('telegram.chat.id',       '',   'Telegram chat ID to send alerts to'),
    ('alert.cpu.threshold',    '90', 'CPU usage percentage threshold for WARNING alert'),
    ('alert.ram.threshold',    '90', 'RAM usage percentage threshold for WARNING alert'),
    ('agent.timeout.seconds',  '60', 'Seconds without heartbeat before marking a node OFFLINE')
ON CONFLICT (key) DO NOTHING;
