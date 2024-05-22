-- v2024.5.2
USE dinghy_racing;

-- clear existing data
DELETE FROM entry_laps;
DELETE FROM lap;
DELETE FROM entry;
DELETE FROM race;
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

INSERT INTO dinghy_class (id, name, crew_size, version) VALUES (1, "Scorpion", 2, 0);
INSERT INTO dinghy_class (id, name, crew_size, version) VALUES (2, "Graduate", 2, 0);
INSERT INTO dinghy_class (id, name, crew_size, version) VALUES (3, "Comet", 1, 0);

INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (1, "1234", 1, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (2, "2726", 2, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (3, "6745", 1, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (4, "2928", 2, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (5, "826", 3, 0);

INSERT INTO race (id, name, duration, planned_laps, planned_start_time, dinghy_class_id, start_sequence_state, version) VALUES (1, "Scorpion A", 2700000000000, 5, "2024-01-07 14:10:00", 1, start_sequence_state = "NONE", 0);
INSERT INTO race (id, name, duration, planned_laps, planned_start_time, dinghy_class_id, start_sequence_state, version) VALUES (2, "Graduate A", 2700000000000, 4, "2024-01-07 14:30:00", 2, start_sequence_state = "NONE", 0);
INSERT INTO race (id, name, duration, planned_laps, planned_start_time, dinghy_class_id, start_sequence_state, version) VALUES (3, "Comet A", 2700000000000, 4, "2024-10-07 14:30:00", 3, start_sequence_state = "NONE", 0);
INSERT INTO race (id, name, duration, planned_laps, planned_start_time, dinghy_class_id, start_sequence_state, version) VALUES (4, "Handicap A", 2700000000000, 4, "2024-01-07 14:10:00", null, start_sequence_state = "NONE", 0);

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