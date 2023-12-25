USE dinghy_racing;

CREATE TABLE hibernate_sequence (
	next_val BIGINT
) engine=InnoDB;
INSERT INTO hibernate_sequence VALUES ( 1 );

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
	version BIGINT, 
	CONSTRAINT PK_dinghy_class_id PRIMARY KEY (id),
	CONSTRAINT UK_dinghy_class_name UNIQUE (name)
) engine=InnoDB;
CREATE TABLE dinghy (
	id BIGINT NOT NULL, 
	sail_number VARCHAR(255) NOT NULL, 
	version BIGINT, 
	dinghy_class_id BIGINT NOT NULL, 
	CONSTRAINT PK_dinghy_id PRIMARY KEY (id),
	CONSTRAINT UK_dinghy_dinghy_class_id_sail_number UNIQUE (dinghy_class_id, sail_number),
	CONSTRAINT FK_dinghy_dinghy_class_id FOREIGN KEY (dinghy_class_id) REFERENCES dinghy_class (id)
) engine=InnoDB;
CREATE TABLE race (
	id BIGINT NOT NULL, 
	actual_start_time DATETIME(6), 
	duration BIGINT NOT NULL, 
	name VARCHAR(255) NOT NULL, 
	planned_laps integer NOT NULL, 
	planned_start_time DATETIME(6) NOT NULL, 
	version BIGINT, 
	dinghy_class_id BIGINT, 
	CONSTRAINT PK_race_id PRIMARY KEY (id),
	CONSTRAINT UK_race_name_planned_start_time UNIQUE (name, planned_start_time),
	CONSTRAINT FK_race_dinghy_class_id FOREIGN KEY (dinghy_class_id) REFERENCES dinghy_class (id)
) engine=InnoDB;
CREATE TABLE entry (
	id BIGINT NOT NULL, 
	version BIGINT, 
	competitor_id BIGINT NOT NULL, 
	dinghy_id BIGINT NOT NULL, 
	race_id BIGINT NOT NULL, 
	CONSTRAINT PK_entry_id PRIMARY KEY (id),
	CONSTRAINT UK_entry_competitor_id_dinghy_id_race_id UNIQUE (competitor_id, dinghy_id, race_id),
	CONSTRAINT UK_entry_competitor_id_race_id UNIQUE (competitor_id, race_id),
	CONSTRAINT UK_entry_dinghy_id_race_id UNIQUE (dinghy_id, race_id),
	CONSTRAINT FK_entry_competitor_id FOREIGN KEY (competitor_id) REFERENCES competitor (id),
	CONSTRAINT FK_entry_dinghy_id FOREIGN KEY (dinghy_id) REFERENCES dinghy (id),
	CONSTRAINT FK_entry_race_id FOREIGN KEY (race_id) REFERENCES race (id)
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
	CONSTRAINT UK_entry_laps_laps_id UNIQUE (laps_id),
	CONSTRAINT FK_entry_laps_laps_id FOREIGN KEY (laps_id) REFERENCES lap (id),
	CONSTRAINT FK_entry_laps_entry_id FOREIGN KEY (entry_id) REFERENCES entry (id)
) engine=InnoDB;