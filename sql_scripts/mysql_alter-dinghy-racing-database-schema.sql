-- v2024.1.1 to v2024.2.2
USE dinghy_racing;

ALTER TABLE entry ADD COLUMN scoring_abbreviation CHAR(3) NULL;

-- v2024.2.2 to v2024.3.1
USE dinghy_racing;

ALTER TABLE race ADD COLUMN start_sequence_state VARCHAR(50) NULL;

-- v2024.5.1 to v2024.5.2
USE dinghy_racing;

CREATE TABLE competitor_seq (next_val BIGINT) engine=InnoDB;
INSERT INTO competitor_seq (next_val) SELECT MAX(id) + 50 FROM competitor;

CREATE TABLE dinghy_seq (next_val BIGINT) engine=InnoDB;
INSERT INTO dinghy_seq (next_val) SELECT MAX(id) + 50 FROM dinghy;

CREATE TABLE dinghy_class_seq (next_val BIGINT) engine=InnoDB;
INSERT INTO dinghy_class_seq (next_val) SELECT MAX(id) + 50 FROM dinghy_class;

CREATE TABLE entry_seq (next_val BIGINT) engine=InnoDB;
INSERT INTO entry_seq (next_val) SELECT MAX(id) + 50 FROM entry;

CREATE TABLE lap_seq (next_val BIGINT) engine=InnoDB;
INSERT INTO lap_seq (next_val) SELECT MAX(id) + 50 FROM lap;

CREATE TABLE race_seq (next_val BIGINT) engine=InnoDB;
INSERT INTO race_seq (next_val) SELECT MAX(id) + 50 FROM race;

DROP TABLE hibernate_sequence;

-- v2024.5.2 to v2024.5.3
USE dinghy_racing;

ALTER TABLE race DROP COLUMN actual_start_time;

-- v2024.5.3 to v2024.7.1
ALTER TABLE dinghy_class ADD COLUMN portsmouth_number SMALLINT;
ALTER TABLE race ADD COLUMN `type` VARCHAR(50) NOT NULL;
ALTER TABLE entry ADD COLUMN position SMALLINT NULL;

-- v2024.7.1 to v2024.8.1
ALTER TABLE race ADD COLUMN start_type VARCHAR(50) NOT NULL;

-- v2024.8.1 to v2024.12.2
ALTER TABLE entry ADD COLUMN corrected_time BIGINT;