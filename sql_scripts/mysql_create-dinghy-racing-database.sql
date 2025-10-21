-- v2025.9.2
CREATE DATABASE IF NOT EXISTS `dinghy_racing` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE dinghy_racing;

CREATE TABLE competitor_seq (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO competitor_seq VALUES ( 1 );

CREATE TABLE dinghy_seq (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO dinghy_seq VALUES ( 1 );

CREATE TABLE dinghy_class_seq (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO dinghy_class_seq VALUES ( 1 );

CREATE TABLE fleet_seq (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO fleet_seq VALUES ( 1 );

CREATE TABLE entry_seq (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO entry_seq VALUES ( 1 );

CREATE TABLE lap_seq (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO lap_seq VALUES ( 1 );

CREATE TABLE race_seq (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO race_seq VALUES ( 1 );

CREATE TABLE signed_up_seq (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO signed_up_seq VALUES ( 1 );

CREATE TABLE competitor (
	id BIGINT NOT NULL, 
	name VARCHAR(255) NOT NULL, 
	version BIGINT, 
	CONSTRAINT PK_competitor_id PRIMARY KEY (id),
	CONSTRAINT UK_competitor_name UNIQUE (name)
) engine=InnoDB;

CREATE TABLE dinghy_class (
	id BIGINT NOT NULL, 
	name VARCHAR(255) NOT NULL,
    crew_size TINYINT NOT NULL,
	portsmouth_number SMALLINT NOT NULL,
	external_name VARCHAR(255),
	version BIGINT, 
	CONSTRAINT PK_dinghy_class_id PRIMARY KEY (id),
	CONSTRAINT UK_dinghy_class_name UNIQUE (name)
) engine=InnoDB;

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
	CONSTRAINT UK_fleet_dinghy_classes_fleet_id_dinghy_classes_id UNIQUE (fleet_id, dinghy_classes_id)
) engine=InnoDB;

CREATE TABLE dinghy (
	id BIGINT NOT NULL, 
	sail_number VARCHAR(255) NOT NULL, 
	dinghy_class_id BIGINT NOT NULL, 
	version BIGINT, 
	CONSTRAINT PK_dinghy_id PRIMARY KEY (id),
	CONSTRAINT UK_dinghy_dinghy_class_id_sail_number UNIQUE (dinghy_class_id, sail_number)
) engine=InnoDB;

CREATE TABLE race (
	id BIGINT NOT NULL, 
	fleet_id BIGINT NOT NULL, 
	name VARCHAR(255) NOT NULL, 
	last_lead_entry_id BIGINT UNIQUE, 
	last_lead_entry_laps_completed INTEGER, 
	version BIGINT, 
	CONSTRAINT PK_race_id PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE direct_race (
	id BIGINT NOT NULL, 
	duration NUMERIC(21,0) NOT NULL, 
	planned_laps INTEGER NOT NULL, 
	planned_start_time TIMESTAMP(6) NOT NULL, 
	type ENUM ('FLEET','PURSUIT') NOT NULL, 
	start_type ENUM ('CSCCLUBSTART','RRS26') NOT NULL, 
	CONSTRAINT PK_direct_race_id PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE entry (
	id BIGINT NOT NULL, 
	helm_id BIGINT NOT NULL UNIQUE, 
	crew_id BIGINT UNIQUE, 
	dinghy_id BIGINT NOT NULL UNIQUE, 
	scoring_abbreviation VARCHAR(3), 
	corrected_time NUMERIC(21,0), 
	on_last_lap BOOLEAN NOT NULL, 
	finished_race BOOLEAN NOT NULL, 
	version BIGINT, 
	CONSTRAINT PK_entry_id PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE signed_up (
	id BIGINT NOT NULL,  
	race_id BIGINT, 
	entry_id BIGINT,
	position INTEGER, 
	version BIGINT, 
	CONSTRAINT PK_signed_up_id PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE lap (
	id BIGINT NOT NULL, 
	number integer NOT NULL, 
	time BIGINT NOT NULL, 
	version BIGINT, 
	CONSTRAINT PK_lap_id PRIMARY KEY (id)
) engine=InnoDB;

CREATE TABLE entry_laps (
	entry_id BIGINT NOT NULL, 
	laps_id BIGINT NOT NULL, 
	CONSTRAINT PK_entry_laps_entry_id_laps_id PRIMARY KEY (entry_id, laps_id),
	CONSTRAINT UK_entry_laps_laps_id UNIQUE (laps_id)
) engine=InnoDB;

ALTER TABLE race ADD CONSTRAINT FK_race_fleet_id FOREIGN KEY (fleet_id) REFERENCES fleet (id);
ALTER TABLE race ADD CONSTRAINT FK_race_last_lead_entry_id FOREIGN KEY (last_lead_entry_id) REFERENCES entry (id);
ALTER TABLE direct_race ADD CONSTRAINT FK_direct_race_race_id FOREIGN KEY (id) REFERENCES race (id);
ALTER TABLE entry ADD CONSTRAINT FK_entry_helm_id FOREIGN KEY (helm_id) REFERENCES competitor (id);
ALTER TABLE entry ADD CONSTRAINT FK_entry_crew_id FOREIGN KEY (crew_id) REFERENCES competitor (id);
ALTER TABLE entry ADD CONSTRAINT FK_entry_dinghy_id FOREIGN KEY (dinghy_id) REFERENCES dinghy (id);
ALTER TABLE signed_up ADD CONSTRAINT FK_signed_up_race_id FOREIGN KEY (race_id) REFERENCES race (id);
ALTER TABLE signed_up ADD CONSTRAINT FK_signed_up_entry_id FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE fleet_dinghy_classes ADD CONSTRAINT FK_fleet_dinghy_classes_fleet_id FOREIGN KEY (fleet_id) REFERENCES fleet (id);
ALTER TABLE fleet_dinghy_classes ADD CONSTRAINT FK_fleet_dinghy_classes_dinghy_classes_id FOREIGN KEY (dinghy_classes_id) REFERENCES dinghy_class (id);
ALTER TABLE dinghy ADD CONSTRAINT FK_dinghy_dinghy_class_id FOREIGN KEY (dinghy_class_id) REFERENCES dinghy_class (id);
ALTER TABLE entry_laps ADD CONSTRAINT FK_entry_laps_laps_id FOREIGN KEY (laps_id) REFERENCES lap (id);
ALTER TABLE entry_laps ADD CONSTRAINT FK_entry_laps_entry_id FOREIGN KEY (entry_id) REFERENCES entry (id);