ALTER TABLE cards
    DROP CONSTRAINT IF EXISTS fk_cards_user_id,
    ADD CONSTRAINT fk_cards_user_id
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE cards
    DROP CONSTRAINT IF EXISTS chk_card_status,
    ADD CONSTRAINT chk_card_status
    CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED'));

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS chk_user_role,
    ADD CONSTRAINT chk_user_role
    CHECK (role IN ('ROLE_ADMIN', 'ROLE_USER'));

CREATE INDEX IF NOT EXISTS idx_cards_status ON cards(status);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

UPDATE users SET role = 'ROLE_ADMIN' WHERE username = 'admin';
UPDATE users SET role = 'ROLE_USER' WHERE username = 'user';