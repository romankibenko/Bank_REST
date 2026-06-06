-- Add last_four_digits column to store the last 4 digits of the original card number for display masking
ALTER TABLE cards ADD COLUMN last_four_digits VARCHAR(4);

-- Fill existing rows with '0000' as a placeholder (existing cards have no original number available)
UPDATE cards SET last_four_digits = '0000' WHERE last_four_digits IS NULL;

-- Make the column NOT NULL after backfill
ALTER TABLE cards ALTER COLUMN last_four_digits SET NOT NULL;
