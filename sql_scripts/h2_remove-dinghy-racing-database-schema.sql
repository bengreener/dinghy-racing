DROP SEQUENCE hibernate_sequence;

ALTER TABLE dinghy DROP CONSTRAINT FK_dinghy_dinghy_class_id;
ALTER TABLE entry DROP CONSTRAINT FK_entry_competitor_id;
ALTER TABLE entry DROP CONSTRAINT FK_entry_dinghy_id;
ALTER TABLE entry DROP CONSTRAINT FK_entry_race_id;
ALTER TABLE entry_laps DROP CONSTRAINT FK_entry_laps_laps_id;
ALTER TABLE entry_laps DROP CONSTRAINT FK_entry_laps_entry_id;
ALTER TABLE race DROP CONSTRAINT FK_race_dinghy_class_id;
ALTER TABLE competitor DROP CONSTRAINT UK_competitor_name;
ALTER TABLE dinghy DROP CONSTRAINT UK_dinghy_dinghy_class_id_sail_number;
ALTER TABLE dinghy_class DROP CONSTRAINT UK_dinghy_class_name;
ALTER TABLE entry DROP CONSTRAINT UK_entry_competitor_id_dinghy_id_race_id;
ALTER TABLE entry DROP CONSTRAINT UK_entry_competitor_id_race_id;
ALTER TABLE entry DROP CONSTRAINT UK_entry_dinghy_id_race_id;
ALTER TABLE entry_laps DROP CONSTRAINT UK_entry_laps_laps_id;
ALTER TABLE race DROP CONSTRAINT UK_race_name_planned_start_time;

DROP TABLE hibernate_sequence;
DROP TABLE competitor;
DROP TABLE dinghy;
DROP TABLE dinghy_class;
DROP TABLE entry_laps;
DROP TABLE entry;
DROP TABLE lap;
DROP TABLE race;