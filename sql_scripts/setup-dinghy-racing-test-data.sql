-- v2025.1.2
USE dinghy_racing;

-- clear existing data
DELETE FROM entry_laps;
DELETE FROM lap;
DELETE FROM entry;
DELETE FROM race;
DELETE FROM fleet_dinghy_classes;
DELETE FROM fleet;
DELETE FROM dinghy;
DELETE FROM dinghy_class;
DELETE FROM competitor;

-- create testing data
INSERT INTO competitor (id, name, version) VALUES (1, "Chris Marshall", 0);
INSERT INTO competitor (id, name, version) VALUES (2, "Sarah Pascal", 0);
INSERT INTO competitor (id, name, version) VALUES (3, "Jill Myer", 0);
INSERT INTO competitor (id, name, version) VALUES (4, "Lou Screw", 0);
INSERT INTO competitor (id, name, version) VALUES (5, "Liu Bao", 0);
INSERT INTO competitor (id, name, version) VALUES (6, "Owain Davies", 0);

INSERT INTO dinghy_class (id, name, crew_size, portsmouth_number, external_name, version) VALUES (1, "Scorpion", 2, 1043, null, 0);
INSERT INTO dinghy_class (id, name, crew_size, portsmouth_number, external_name, version) VALUES (2, "Graduate", 2, 1110, "GRADUATE", 0);
INSERT INTO dinghy_class (id, name, crew_size, portsmouth_number, external_name, version) VALUES (3, "Comet", 1, 1210, null, 0);

INSERT INTO fleet (id, name) VALUES (1, "Scorpion");
INSERT INTO fleet (id, name) VALUES (2, "Graduate");
INSERT INTO fleet (id, name) VALUES (3, "Comet");
INSERT INTO fleet (id, name) VALUES (4, "Handicap");

INSERT INTO fleet_dinghy_classes (fleet_id, dinghy_classes_id) VALUES (1, 1);
INSERT INTO fleet_dinghy_classes (fleet_id, dinghy_classes_id) VALUES (2, 2);
INSERT INTO fleet_dinghy_classes (fleet_id, dinghy_classes_id) VALUES (3, 3);

INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (1, "1234", 1, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (2, "2726", 2, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (3, "6745", 1, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (4, "2928", 2, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (5, "826", 3, 0);

INSERT INTO race (id, name, duration, planned_laps, planned_start_time, fleet_id, `type`, start_sequence_state, start_type, version) 
	VALUES (1, "Scorpion A", 2700000000000, 5, "2024-12-09 14:10:00", 1, "FLEET", "NONE", "CSCCLUBSTART", 0);
INSERT INTO race (id, name, duration, planned_laps, planned_start_time, fleet_id, `type`, start_sequence_state, start_type, version) 
	VALUES (2, "Graduate A", 2700000000000, 4, "2024-12-09 14:30:00", 2, "FLEET", "NONE", "CSCCLUBSTART", 0);
INSERT INTO race (id, name, duration, planned_laps, planned_start_time, fleet_id, `type`, start_sequence_state, start_type, version) 
	VALUES (3, "Comet A", 2700000000000, 4, "2024-12-09 14:30:00", 3, "FLEET", "NONE", "CSCCLUBSTART", 0);
INSERT INTO race (id, name, duration, planned_laps, planned_start_time, fleet_id, `type`, start_sequence_state, start_type, version) 
	VALUES (4, "Handicap A", 2700000000000, 4, "2024-12-09 14:10:00", 4, "FLEET", "NONE", "CSCCLUBSTART", 0);

INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (1, 1, 1, 1, 4, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (2, 2, 3, 1, 6, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (3, 3, 4, 2, null, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (4, 3, 5, 3, null, 0);	
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (5, 1, 1, 4, 4, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (6, 3, 5, 4, null, 0);

UPDATE competitor_seq SET next_val = (SELECT MAX(id) + 50 FROM competitor);
UPDATE dinghy_seq SET next_val = (SELECT MAX(id) + 50 FROM dinghy);
UPDATE dinghy_class_seq SET next_val = (SELECT MAX(id) + 50 FROM dinghy_class);
UPDATE entry_seq SET next_val = (SELECT MAX(id) + 50 FROM entry);
UPDATE lap_seq SET next_val = 1;
UPDATE race_seq SET next_val = (SELECT MAX(id) + 50 FROM race);
UPDATE fleet_seq SET next_val = (SELECT MAX(id) + 50 FROM fleet);