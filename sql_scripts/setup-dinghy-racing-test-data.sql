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
INSERT INTO competitor (id, name, version) VALUES (8, "Chris Marshall", 0);
INSERT INTO competitor (id, name, version) VALUES (9, "Sarah Pascal", 0);
INSERT INTO competitor (id, name, version) VALUES (15, "Jill Myer", 0);
INSERT INTO competitor (id, name, version) VALUES (12, "Lou Screw", 0);
INSERT INTO competitor (id, name, version) VALUES (14, "Liu Bao", 0);
INSERT INTO competitor (id, name, version) VALUES (13, "Owain Davies", 0);

INSERT INTO dinghy_class (id, name, crew_size, version) VALUES (1, "Scorpion", 2, 0);
INSERT INTO dinghy_class (id, name, crew_size, version) VALUES (5, "Graduate", 2, 0);
INSERT INTO dinghy_class (id, name, crew_size, version) VALUES (16, "Comet", 1, 0);

INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (2, "1234", 1, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (6, "2726", 5, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (3, "6745", 1, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (7, "2928", 5, 0);
INSERT INTO dinghy (id, sail_number, dinghy_class_id, version) VALUES (18, "826", 16, 0);

INSERT INTO race (id, name, duration, planned_laps, planned_start_time, dinghy_class_id, version) VALUES (4, "Scorpion A", 2700000000000, 5, "2024-01-07 14:10:00", 1, 0);
INSERT INTO race (id, name, duration, planned_laps, planned_start_time, dinghy_class_id, version) VALUES (7, "Graduate A", 2700000000000, 4, "2024-01-07 14:30:00", 5, 0);
INSERT INTO race (id, name, duration, planned_laps, planned_start_time, dinghy_class_id, version) VALUES (17, "Comet A", 2700000000000, 4, "2024-10-07 14:30:00", 16, 0);
INSERT INTO race (id, name, duration, planned_laps, planned_start_time, dinghy_class_id, version) VALUES (8, "Handicap A", 2700000000000, 4, "2024-01-07 14:10:00", null, 0);

INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES ( 10, 8, 2, 4, 12, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES ( 11, 9, 3, 4, 13, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES ( 12, 15, 7, 7, null, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES ( 19, 15, 18, 17, null, 0);	
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES ( 20, 8, 2, 8, 12, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES ( 21, 15, 18, 8, null, 0);

UPDATE hibernate_sequence SET next_val = 22;