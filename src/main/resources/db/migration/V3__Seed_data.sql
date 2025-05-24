INSERT INTO users (username, password, role, active, holder_name, created_at, updated_at)
VALUES
(
  'admin',
  '$2a$12$CrpWn.tVGtgMKHyCpqep0Ow/jTyeUNYwR9VRWjv2V/2GfGSCAdl52',
  'ROLE_ADMIN',
  true,
  'Admin Holder',
  NOW(),
  NOW()
),
(
  'user',
  '$2a$12$HpWE5.YBX7fu.t58/Irb5.g6kTf5tFQ3CPBHSxC2vnqCDDJSLcnI2',
  'ROLE_USER',
  true,
  'User Holder',
  NOW(),
  NOW()
);

INSERT INTO cards (card_number, holder_name, expiry_date, status, balance, user_id, created_at, updated_at)
VALUES
('5454545454545454', 'Roman Volkov', '2030-12-31', 'ACTIVE', 1000.00, 1, NOW(), NOW());