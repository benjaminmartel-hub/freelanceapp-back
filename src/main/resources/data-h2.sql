-- Demo user for product showcase
MERGE INTO users (name, email)
KEY (email)
VALUES ('demo', 'demo@freelanceos.com');

-- Demo authentication account (username: demo, password: demo1234)
MERGE INTO auth_accounts (username, password_hash, provider, provider_user_id)
KEY (username)
VALUES ('demo', '$2a$10$iif.9MpNXoLSOpcZgNRu7OT9LJaQzl1gk.T/nK4EtpiLBpmYaQX1K', 'LOCAL', NULL);

-- Fiscal configuration for tax estimation block
INSERT INTO fiscal_configs (user_id, tax_rate, vat_enabled, declaration_period)
SELECT u.id, 0.2200, TRUE, 'MONTHLY'
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM fiscal_configs fc WHERE fc.user_id = u.id
  );

-- Missions (active + finished) for "expiring missions" and portfolio view
INSERT INTO missions (user_id, title, client_name, end_date, status)
SELECT u.id, 'Refonte site e-commerce', 'Maison Beldi', DATEADD('DAY', 9, CURRENT_DATE), 'ACTIVE'
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Refonte site e-commerce'
        AND m.client_name = 'Maison Beldi'
  );

INSERT INTO missions (user_id, title, client_name, end_date, status)
SELECT u.id, 'Optimisation SEO technique', 'Atelier Nova', DATEADD('DAY', 14, CURRENT_DATE), 'ACTIVE'
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Optimisation SEO technique'
        AND m.client_name = 'Atelier Nova'
  );

INSERT INTO missions (user_id, title, client_name, end_date, status)
SELECT u.id, 'Migration CRM', 'Blue Orbit', DATEADD('DAY', -30, CURRENT_DATE), 'FINISHED'
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM missions m
      WHERE m.user_id = u.id
        AND m.title = 'Migration CRM'
        AND m.client_name = 'Blue Orbit'
  );

-- Invoices for dashboard metrics and monthly history
INSERT INTO invoices (user_id, client_name, status, due_date, total_ht, total_ttc)
SELECT u.id, 'Maison Beldi', 'PAID', DATEADD('DAY', -5, CURRENT_DATE), 2800.00, 3360.00
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.client_name = 'Maison Beldi'
        AND i.status = 'PAID'
        AND i.due_date = DATEADD('DAY', -5, CURRENT_DATE)
  );

INSERT INTO invoices (user_id, client_name, status, due_date, total_ht, total_ttc)
SELECT u.id, 'Atelier Nova', 'SENT', DATEADD('DAY', 7, CURRENT_DATE), 1600.00, 1920.00
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.client_name = 'Atelier Nova'
        AND i.status = 'SENT'
        AND i.due_date = DATEADD('DAY', 7, CURRENT_DATE)
  );

INSERT INTO invoices (user_id, client_name, status, due_date, total_ht, total_ttc)
SELECT u.id, 'Blue Orbit', 'SENT', DATEADD('DAY', -12, CURRENT_DATE), 2400.00, 2880.00
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.client_name = 'Blue Orbit'
        AND i.status = 'SENT'
        AND i.due_date = DATEADD('DAY', -12, CURRENT_DATE)
  );

INSERT INTO invoices (user_id, client_name, status, due_date, total_ht, total_ttc)
SELECT u.id, 'Studio Atlas', 'PAID', DATEADD('MONTH', -1, CURRENT_DATE), 1900.00, 2280.00
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.client_name = 'Studio Atlas'
        AND i.status = 'PAID'
        AND i.due_date = DATEADD('MONTH', -1, CURRENT_DATE)
  );

INSERT INTO invoices (user_id, client_name, status, due_date, total_ht, total_ttc)
SELECT u.id, 'Cobalt Labs', 'PAID', DATEADD('MONTH', -2, CURRENT_DATE), 2250.00, 2700.00
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.client_name = 'Cobalt Labs'
        AND i.status = 'PAID'
        AND i.due_date = DATEADD('MONTH', -2, CURRENT_DATE)
  );

INSERT INTO invoices (user_id, client_name, status, due_date, total_ht, total_ttc)
SELECT u.id, 'Pixel Harbor', 'PAID', DATEADD('MONTH', -4, CURRENT_DATE), 3100.00, 3720.00
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.client_name = 'Pixel Harbor'
        AND i.status = 'PAID'
        AND i.due_date = DATEADD('MONTH', -4, CURRENT_DATE)
  );

INSERT INTO invoices (user_id, client_name, status, due_date, total_ht, total_ttc)
SELECT u.id, 'Nordic Scale', 'PAID', DATEADD('MONTH', -7, CURRENT_DATE), 1700.00, 2040.00
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.client_name = 'Nordic Scale'
        AND i.status = 'PAID'
        AND i.due_date = DATEADD('MONTH', -7, CURRENT_DATE)
  );

INSERT INTO invoices (user_id, client_name, status, due_date, total_ht, total_ttc)
SELECT u.id, 'Nova Factory', 'DRAFT', DATEADD('DAY', 20, CURRENT_DATE), 1300.00, 1560.00
FROM users u
WHERE u.email = 'demo@freelanceos.com'
  AND NOT EXISTS (
      SELECT 1 FROM invoices i
      WHERE i.user_id = u.id
        AND i.client_name = 'Nova Factory'
        AND i.status = 'DRAFT'
        AND i.due_date = DATEADD('DAY', 20, CURRENT_DATE)
  );
