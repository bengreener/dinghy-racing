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

-- v2024.12.2 to v2025.1.2
CREATE TABLE fleet (
	id BIGINT NOT NULL, 
	name VARCHAR(255) NOT NULL,
	version BIGINT, 
	CONSTRAINT PK_fleet_id PRIMARY KEY (id),
	CONSTRAINT UK_fleet_name UNIQUE (name)
) engine=InnoDB;

CREATE TABLE fleet_dinghy_classes (
	fleet_id BIGINT NOT NULL,
	dinghy_classes_id BIGINT NOT NULL,
	CONSTRAINT PK_fleet_dinghy_classes_fleet_id_dinghy_classes_id PRIMARY KEY (fleet_id, dinghy_classes_id),
	CONSTRAINT FK_fleet_fleet_id FOREIGN KEY (fleet_id) REFERENCES fleet (id),
	CONSTRAINT FK_dinghy_class_dinghy_classes_id FOREIGN KEY (dinghy_classes_id) REFERENCES dinghy_class (id),
	CONSTRAINT UK_fleet_dinghy_classes_fleet_id_dinghy_classes_id UNIQUE (fleet_id, dinghy_classes_id)
) engine=InnoDB;

CREATE TABLE fleet_seq (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO fleet_seq VALUES ( 1 );

-- exisitng data needs race records to be mapped to fleet records so migration will be more complicated
--	1) Determine Race to fleet mappings for existing data
--	2) Generate script to map race records to fleet records
--	3) Drop race.dinghy_class_id table
--	4) add race.fleet_id column without NOT NULL constraint
--	5) update race.fleet_id with fleet ids
--	6) update race table to set NOT NULL constraint on fleet_id

ALTER TABLE race 
	DROP CONSTRAINT FK_race_dinghy_class_id,
	DROP COLUMN dinghy_class_id;

ALTER TABLE race 
	ADD COLUMN fleet_id BIGINT NOT NULL,
	ADD CONSTRAINT FK_race_fleet_id FOREIGN KEY (fleet_id) REFERENCES fleet (id);
	
ALTER TABLE dinghy_class
	ADD COLUMN external_name VARCHAR(255),
	MODIFY COLUMN portsmouth_number SMALLINT NOT NULL,
	DROP COLUMN start_sequence_state;