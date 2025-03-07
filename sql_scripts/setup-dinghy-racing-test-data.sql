-- v2025.1.2
USE dinghy_racing;

-- clear existing data
DELETE FROM entry_laps;
DELETE FROM lap;
DELETE FROM entry;
DELETE FROM embedded_race_hosts;
DELETE FROM direct_race;
DELETE FROM embedded_race;
DELETE FROM race;
DELETE FROM fleet_dinghy_classes;
DELETE FROM fleet;
DELETE FROM dinghy;
DELETE FROM dinghy_class;
DELETE FROM competitor;
DELETE FROM competitor_seq;
DELETE FROM dinghy_seq;
DELETE FROM dinghy_class_seq;
DELETE FROM entry_seq;
DELETE FROM lap_seq;
DELETE FROM race_seq;
DELETE FROM fleet_seq;

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

-- create direct races
INSERT INTO race (id, name, version) 
	VALUES (1, "Scorpion A", 0);
INSERT INTO race (id, name, version) 
	VALUES (2, "Graduate A", 0);
INSERT INTO race (id, name, version) 
	VALUES (3, "Comet A", 0);
INSERT INTO race (id, name, version) 
	VALUES (4, "Handicap A", 0);
	
INSERT INTO direct_race (id, duration, planned_laps, planned_start_time, fleet_id, `type`, start_type) 
	VALUES (1, 2700000000000, 5, "2024-12-09 14:10:00", 1, "FLEET", "CSCCLUBSTART");
INSERT INTO direct_race (id, duration, planned_laps, planned_start_time, fleet_id, `type`, start_type) 
	VALUES (2, 2700000000000, 4, "2024-12-09 14:15:00", 2, "FLEET", "CSCCLUBSTART");
INSERT INTO direct_race (id, duration, planned_laps, planned_start_time, fleet_id, `type`, start_type) 
	VALUES (3, 2700000000000, 4, "2024-12-09 14:20:00", 3, "FLEET", "CSCCLUBSTART");
INSERT INTO direct_race (id, duration, planned_laps, planned_start_time, fleet_id, `type`, start_type) 
	VALUES (4, 2700000000000, 4, "2024-12-09 14:25:00", 4, "FLEET", "CSCCLUBSTART");
	
-- create embedded races
INSERT INTO race (id, name, version) 
	VALUES (5, "Veterans 1", 0);

INSERT INTO embedded_race (id) VALUES (5);
INSERT INTO embedded_race_hosts (embedded_race_id, hosts_id) VALUES (5, 3);
INSERT INTO embedded_race_hosts (embedded_race_id, hosts_id) VALUES (5, 4);

INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (1, 1, 1, 1, 4, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (2, 2, 3, 1, 6, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (3, 3, 4, 2, null, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (4, 3, 5, 3, null, 0);	
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (5, 1, 1, 4, 4, 0);
INSERT INTO entry (id, helm_id, dinghy_id, race_id, crew_id, version) VALUES (6, 3, 5, 4, null, 0);

INSERT INTO competitor_seq SET next_val = (SELECT MAX(id) + 50 FROM competitor);
INSERT INTO dinghy_seq SET next_val = (SELECT MAX(id) + 50 FROM dinghy);
INSERT INTO dinghy_class_seq SET next_val = (SELECT MAX(id) + 50 FROM dinghy_class);
INSERT INTO entry_seq SET next_val = (SELECT MAX(id) + 50 FROM entry);
INSERT INTO lap_seq SET next_val = 1;
INSERT INTO race_seq SET next_val = (SELECT MAX(id) + 50 FROM race);
INSERT INTO fleet_seq SET next_val = (SELECT MAX(id) + 50 FROM fleet);