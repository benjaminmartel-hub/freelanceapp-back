INSERT INTO users (name, email)
SELECT 'Alice Martin', 'alice@example.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@example.com');

INSERT INTO users (name, email)
SELECT 'Bob Dupont', 'bob@example.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@example.com');
