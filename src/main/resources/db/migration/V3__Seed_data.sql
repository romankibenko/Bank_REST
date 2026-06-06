INSERT INTO users (username, password, role, active, holder_name, created_at, updated_at)
VALUES
(
  'admin',
  -- bcrypt hash of 'admin123'
  '$2a$12$QW1V8ucRPFf.uQQS6tll0uF.vyMsYR8/bXbD4C7M9JtGeQ0P2hTsG',
  'ROLE_ADMIN',
  true,
  'Admin Holder',
  NOW(),
  NOW()
),
(
  'user',
  -- bcrypt hash of 'user123'
  '$2a$12$eD.LOzPQgYt.QK/sWFN0ueTGIEs29y4ZYrKDAFx41LwcFYuIFmiuC',
  'ROLE_USER',
  true,
  'User Holder',
  NOW(),
  NOW()
);

INSERT INTO cards (card_number, holder_name, expiry_date, status, balance, user_id, created_at, updated_at)
VALUES
('5454545454545454', 'Roman Volkov', '2030-12-31', 'ACTIVE', 1000.00, 1, NOW(), NOW());