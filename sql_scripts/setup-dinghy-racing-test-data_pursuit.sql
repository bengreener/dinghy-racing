-- v2025.8.2
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

INSERT INTO dinghy_class (id, name, crew_size, portsmouth_number, external_name, version) VALUES (1, "Cadet", 1, 1455, null, 0);
INSERT INTO dinghy_class (id, name, crew_size, portsmouth_number, external_name, version) VALUES (2, "RS Tera", 1, 1445, null, 0);
INSERT INTO dinghy_class (id, name, crew_size, portsmouth_number, external_name, version) VALUES (3, "Topper 4.2", 1, 1440, null, 0);

INSERT INTO fleet (id, name, version) VALUES (1, "Handicap", 0);

INSERT INTO fleet_dinghy_classes (fleet_id, dinghy_classes_id) VALUES (1, 1);
INSERT INTO fleet_dinghy_classes (fleet_id, dinghy_classes_id) VALUES (1, 2);
INSERT INTO fleet_dinghy_classes (fleet_id, dinghy_classes_id) VALUES (1, 3);

INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (1, "123", 1, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (2, "2726", 2, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (3, "2631", 2, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (4, "28345", 3, 0);

INSERT INTO race (id, name, duration, planned_laps, planned_start_time, fleet_id, `type`, start_type, version) 
	VALUES (1, "Pursuit A", 2700000000000, 5, "2025-04-03 9:10:00", 1, "PURSUIT", "RRS26", 0);

INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, on_last_lap, finished_race, version) VALUES (1, 1, 1, 1, null, FALSE, FALSE, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, on_last_lap, finished_race, version) VALUES (2, 2, 2, 1, null, FALSE, FALSE, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, on_last_lap, finished_race, version) VALUES (3, 3, 3, 1, null, FALSE, FALSE, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, on_last_lap, finished_race, version) VALUES (4, 4, 4, 1, null, FALSE, FALSE, 0);

UPDATE competitor_seq SET next_val = (SELECT MAX(id) + 50 FROM competitor);
UPDATE dinghy_seq SET next_val = (SELECT MAX(id) + 50 FROM dinghy);
UPDATE dinghy_class_seq SET next_val = (SELECT MAX(id) + 50 FROM dinghy_class);
UPDATE entry_seq SET next_val = (SELECT MAX(id) + 50 FROM entry);
UPDATE lap_seq SET next_val = 1;
UPDATE race_seq SET next_val = (SELECT MAX(id) + 50 FROM race);
UPDATE fleet_seq SET next_val = (SELECT MAX(id) + 50 FROM fleet);