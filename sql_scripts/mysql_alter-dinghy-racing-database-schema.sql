-- v2024.1.1 to v2024.2.2
USE dinghy_racing;

ALTER TABLE entry ADD COLUMN scoring_abbreviation CHAR(3) NULL;

-- v2024.2.2 to v2024.3.1
USE dinghy_racing;

ALTER TABLE race ADD COLUMN start_sequence_state VARCHAR(50) NULL;
