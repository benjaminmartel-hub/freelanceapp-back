-- Demo user for product showcase
MERGE INTO users (name, email)
KEY (email)
VALUES ('demo', 'demo@freelanceos.com');

-- Demo authentication account (username: demo, password: demo1234)
MERGE INTO auth_accounts (username, password_hash, provider, provider_user_id)
KEY (username)
VALUES ('demo', '$2a$10$iif.9MpNXoLSOpcZgNRu7OT9LJaQzl1gk.T/nK4EtpiLBpmYaQX1K', 'LOCAL', NULL);

MERGE INTO user_roles (user_id, role)
KEY (user_id, role)
VALUES ((SELECT id FROM users WHERE email = 'demo@freelanceos.com'), 'ROLE_USER');

-- Fiscal configuration for tax estimation block
INSERT INTO fiscal_configs (user_id, tax_rate, vat_enabled, declaration_period)
SELECT u.id, 0.2200, TRUE, 'MONTHLY'
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM fiscal_configs fc WHERE fc.user_id = u.id
  );

-- Clients
INSERT INTO clients (user_id, name, contact_email, created_at, updated_at)
SELECT u.id, 'Maison Beldi', 'contact@maisonbeldi.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM clients c WHERE c.user_id = u.id AND c.name = 'Maison Beldi'
  );

INSERT INTO clients (user_id, name, contact_email, created_at, updated_at)
SELECT u.id, 'Atelier Nova', 'hello@ateliernova.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM clients c WHERE c.user_id = u.id AND c.name = 'Atelier Nova'
  );

INSERT INTO clients (user_id, name, contact_email, created_at, updated_at)
SELECT u.id, 'Blue Orbit', 'ops@blueorbit.io', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM clients c WHERE c.user_id = u.id AND c.name = 'Blue Orbit'
  );

INSERT INTO clients (user_id, name, contact_email, created_at, updated_at)
SELECT u.id, 'Studio Atlas', 'hello@studioatlas.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM clients c WHERE c.user_id = u.id AND c.name = 'Studio Atlas'
  );

INSERT INTO clients (user_id, name, contact_email, created_at, updated_at)
SELECT u.id, 'Cobalt Labs', 'contact@cobaltlabs.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM clients c WHERE c.user_id = u.id AND c.name = 'Cobalt Labs'
  );

INSERT INTO clients (user_id, name, contact_email, created_at, updated_at)
SELECT u.id, 'Pixel Harbor', 'hello@pixelharbor.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM clients c WHERE c.user_id = u.id AND c.name = 'Pixel Harbor'
  );

INSERT INTO clients (user_id, name, contact_email, created_at, updated_at)
SELECT u.id, 'Nordic Scale', 'contact@nordicscale.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM clients c WHERE c.user_id = u.id AND c.name = 'Nordic Scale'
  );

INSERT INTO clients (user_id, name, contact_email, created_at, updated_at)
SELECT u.id, 'Nova Factory', 'contact@novafactory.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM clients c WHERE c.user_id = u.id AND c.name = 'Nova Factory'
  );

-- Missions (ongoing + finished) for "expiring missions" and portfolio view
INSERT INTO missions (user_id, client_id, title, daily_rate, expected_duration,
                      total_budget_estimated, start_date, end_date, status, billing_type, internal_notes, currency,
                      created_at, updated_at)
SELECT u.id, c.id, 'Refonte site e-commerce', 600.00, 25,
       15000.00, DATEADD('DAY', -10, CURRENT_DATE), DATEADD('DAY', 9, CURRENT_DATE), 'ONGOING', 'TJM',
       'Refonte UX/UI + tunnel de conversion', 'EUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
JOIN clients c ON c.user_id = u.id AND c.name = 'Maison Beldi'
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Refonte site e-commerce'
        AND m.client_id = c.id
  );

INSERT INTO missions (user_id, client_id, title, daily_rate, expected_duration,
                      total_budget_estimated, start_date, end_date, status, billing_type, internal_notes, currency,
                      created_at, updated_at)
SELECT u.id, c.id, 'Optimisation SEO technique', 450.00, 12,
       5400.00, DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', 14, CURRENT_DATE), 'ONGOING', 'TJM',
       'Audit technique + recommandations', 'EUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
JOIN clients c ON c.user_id = u.id AND c.name = 'Atelier Nova'
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Optimisation SEO technique'
        AND m.client_id = c.id
  );

INSERT INTO missions (user_id, client_id, title, daily_rate, expected_duration,
                      total_budget_estimated, start_date, end_date, status, billing_type, internal_notes, currency,
                      created_at, updated_at)
SELECT u.id, c.id, 'Migration CRM', 550.00, 30,
       16500.00, DATEADD('DAY', -60, CURRENT_DATE), DATEADD('DAY', -30, CURRENT_DATE), 'FINISHED', 'TJM',
       'Migration terminee, support post-go live', 'EUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
JOIN clients c ON c.user_id = u.id AND c.name = 'Blue Orbit'
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Migration CRM'
        AND m.client_id = c.id
  );

INSERT INTO missions (user_id, client_id, title, daily_rate, expected_duration,
                      total_budget_estimated, start_date, end_date, status, billing_type, internal_notes, currency,
                      created_at, updated_at)
SELECT u.id, c.id, 'Campagne branding', 500.00, 8,
       4000.00, DATEADD('DAY', -120, CURRENT_DATE), DATEADD('DAY', -90, CURRENT_DATE), 'FINISHED', 'TJM',
       'Identite visuelle et assets', 'EUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
JOIN clients c ON c.user_id = u.id AND c.name = 'Studio Atlas'
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Campagne branding'
        AND m.client_id = c.id
  );

INSERT INTO missions (user_id, client_id, title, daily_rate, expected_duration,
                      total_budget_estimated, start_date, end_date, status, billing_type, internal_notes, currency,
                      created_at, updated_at)
SELECT u.id, c.id, 'Audit produit', 650.00, 6,
       3900.00, DATEADD('DAY', -180, CURRENT_DATE), DATEADD('DAY', -150, CURRENT_DATE), 'FINISHED', 'TJM',
       'Audit et recommandations', 'EUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
JOIN clients c ON c.user_id = u.id AND c.name = 'Cobalt Labs'
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Audit produit'
        AND m.client_id = c.id
  );

INSERT INTO missions (user_id, client_id, title, daily_rate, expected_duration,
                      total_budget_estimated, start_date, end_date, status, billing_type, internal_notes, currency,
                      created_at, updated_at)
SELECT u.id, c.id, 'Optimisation conversion', 580.00, 12,
       6960.00, DATEADD('DAY', -210, CURRENT_DATE), DATEADD('DAY', -170, CURRENT_DATE), 'FINISHED', 'TJM',
       'A/B tests et analytics', 'EUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
JOIN clients c ON c.user_id = u.id AND c.name = 'Pixel Harbor'
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Optimisation conversion'
        AND m.client_id = c.id
  );

INSERT INTO missions (user_id, client_id, title, daily_rate, expected_duration,
                      total_budget_estimated, start_date, end_date, status, billing_type, internal_notes, currency,
                      created_at, updated_at)
SELECT u.id, c.id, 'Roadmap data', 520.00, 7,
       3640.00, DATEADD('DAY', -300, CURRENT_DATE), DATEADD('DAY', -260, CURRENT_DATE), 'FINISHED', 'TJM',
       'Planification data platform', 'EUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
JOIN clients c ON c.user_id = u.id AND c.name = 'Nordic Scale'
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Roadmap data'
        AND m.client_id = c.id
  );

INSERT INTO missions (user_id, client_id, title, daily_rate, expected_duration,
                      total_budget_estimated, start_date, end_date, status, billing_type, internal_notes, currency,
                      created_at, updated_at)
SELECT u.id, c.id, 'Prototype MVP', 480.00, 15,
       7200.00, DATEADD('DAY', -15, CURRENT_DATE), DATEADD('DAY', 20, CURRENT_DATE), 'ONGOING', 'TJM',
       'Prototype fonctionnel', 'EUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
JOIN clients c ON c.user_id = u.id AND c.name = 'Nova Factory'
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Prototype MVP'
        AND m.client_id = c.id
  );

-- Invoices for dashboard metrics and monthly history
INSERT INTO invoices (user_id, mission_id, invoice_number, status, issue_date, due_date, total_ht, vat_rate, total_ttc, created_at, updated_at)
SELECT u.id,
       (SELECT MIN(m.id)
        FROM missions m
        JOIN clients c ON c.id = m.client_id
        WHERE m.user_id = u.id AND c.name = 'Maison Beldi'),
       'FAC-2026-0001', 'PAID', DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', -5, CURRENT_DATE), 2800.00, 20.0000, 3360.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.invoice_number = 'FAC-2026-0001'
  );

INSERT INTO invoices (user_id, mission_id, invoice_number, status, issue_date, due_date, total_ht, vat_rate, total_ttc, created_at, updated_at)
SELECT u.id,
       (SELECT MIN(m.id)
        FROM missions m
        JOIN clients c ON c.id = m.client_id
        WHERE m.user_id = u.id AND c.name = 'Atelier Nova'),
       'FAC-2026-0002', 'SENT', DATEADD('DAY', 7, CURRENT_DATE), DATEADD('DAY', 7, CURRENT_DATE), 1600.00, 20.0000, 1920.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.invoice_number = 'FAC-2026-0002'
  );

INSERT INTO invoices (user_id, mission_id, invoice_number, status, issue_date, due_date, total_ht, vat_rate, total_ttc, created_at, updated_at)
SELECT u.id,
       (SELECT MIN(m.id)
        FROM missions m
        JOIN clients c ON c.id = m.client_id
        WHERE m.user_id = u.id AND c.name = 'Blue Orbit'),
       'FAC-2026-0003', 'OVERDUE', DATEADD('DAY', -12, CURRENT_DATE), DATEADD('DAY', -12, CURRENT_DATE), 2400.00, 20.0000, 2880.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.invoice_number = 'FAC-2026-0003'
  );

INSERT INTO invoices (user_id, mission_id, invoice_number, status, issue_date, due_date, total_ht, vat_rate, total_ttc, created_at, updated_at)
SELECT u.id,
       (SELECT MIN(m.id)
        FROM missions m
        JOIN clients c ON c.id = m.client_id
        WHERE m.user_id = u.id AND c.name = 'Studio Atlas'),
       'FAC-2026-0004', 'PAID', DATEADD('MONTH', -1, CURRENT_DATE), DATEADD('MONTH', -1, CURRENT_DATE), 1900.00, 20.0000, 2280.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.invoice_number = 'FAC-2026-0004'
  );

INSERT INTO invoices (user_id, mission_id, invoice_number, status, issue_date, due_date, total_ht, vat_rate, total_ttc, created_at, updated_at)
SELECT u.id,
       (SELECT MIN(m.id)
        FROM missions m
        JOIN clients c ON c.id = m.client_id
        WHERE m.user_id = u.id AND c.name = 'Cobalt Labs'),
       'FAC-2026-0005', 'PAID', DATEADD('MONTH', -2, CURRENT_DATE), DATEADD('MONTH', -2, CURRENT_DATE), 2250.00, 20.0000, 2700.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.invoice_number = 'FAC-2026-0005'
  );

INSERT INTO invoices (user_id, mission_id, invoice_number, status, issue_date, due_date, total_ht, vat_rate, total_ttc, created_at, updated_at)
SELECT u.id,
       (SELECT MIN(m.id)
        FROM missions m
        JOIN clients c ON c.id = m.client_id
        WHERE m.user_id = u.id AND c.name = 'Pixel Harbor'),
       'FAC-2026-0006', 'PAID', DATEADD('MONTH', -4, CURRENT_DATE), DATEADD('MONTH', -4, CURRENT_DATE), 3100.00, 20.0000, 3720.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.invoice_number = 'FAC-2026-0006'
  );

INSERT INTO invoices (user_id, mission_id, invoice_number, status, issue_date, due_date, total_ht, vat_rate, total_ttc, created_at, updated_at)
SELECT u.id,
       (SELECT MIN(m.id)
        FROM missions m
        JOIN clients c ON c.id = m.client_id
        WHERE m.user_id = u.id AND c.name = 'Nordic Scale'),
       'FAC-2026-0007', 'PAID', DATEADD('MONTH', -7, CURRENT_DATE), DATEADD('MONTH', -7, CURRENT_DATE), 1700.00, 20.0000, 2040.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.invoice_number = 'FAC-2026-0007'
  );

INSERT INTO invoices (user_id, mission_id, invoice_number, status, issue_date, due_date, total_ht, vat_rate, total_ttc, created_at, updated_at)
SELECT u.id,
       (SELECT MIN(m.id)
        FROM missions m
        JOIN clients c ON c.id = m.client_id
        WHERE m.user_id = u.id AND c.name = 'Nova Factory'),
       'FAC-2026-0008', 'DRAFT', DATEADD('DAY', 20, CURRENT_DATE), DATEADD('DAY', 20, CURRENT_DATE), 1300.00, 20.0000, 1560.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.invoice_number = 'FAC-2026-0008'
  );
