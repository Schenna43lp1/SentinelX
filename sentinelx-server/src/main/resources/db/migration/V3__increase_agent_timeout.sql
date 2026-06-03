-- Increase default agent timeout from 60s to 90s.
-- With a 30s agent interval, 60s was too tight; 90s gives 3x headroom.
UPDATE settings SET value = '90' WHERE key = 'agent.timeout.seconds' AND value = '60';
