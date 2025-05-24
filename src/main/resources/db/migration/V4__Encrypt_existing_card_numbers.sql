ALTER TABLE cards
    ADD COLUMN encrypted_number VARCHAR(255);
ALTER TABLE cards
    DROP COLUMN card_number;
ALTER TABLE cards
    RENAME COLUMN encrypted_number TO card_number;