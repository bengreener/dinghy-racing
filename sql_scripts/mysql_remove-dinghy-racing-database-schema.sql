-- v2026.5.1

ALTER TABLE signed_up DROP CONSTRAINT FK_signed_up_race_id;
ALTER TABLE signed_up DROP CONSTRAINT FK_signed_up_entry_id;
ALTER TABLE embedded_race_hosts DROP CONSTRAINT FK_embedded_race_hosts_hosts_id;
ALTER TABLE entry DROP CONSTRAINT FK_entry_helm_id;
ALTER TABLE entry DROP CONSTRAINT FK_entry_dinghy_id;
ALTER TABLE entry DROP CONSTRAINT FK_entry_crew_id;
ALTER TABLE entry_laps DROP CONSTRAINT FK_entry_laps_laps_id;
ALTER TABLE entry_laps DROP CONSTRAINT FK_entry_laps_entry_id;
ALTER TABLE fleet_dinghy_classes DROP CONSTRAINT FK_fleet_dinghy_classes_fleet_id;
ALTER TABLE fleet_dinghy_classes DROP CONSTRAINT FK_fleet_dinghy_classes_dinghy_classes_id;
ALTER TABLE race DROP CONSTRAINT FK_race_fleet_id;
ALTER TABLE race DROP CONSTRAINT FK_race_last_lead_entry_id;
ALTER TABLE direct_race DROP CONSTRAINT FK_direct_race_race_id;
ALTER TABLE competitor DROP CONSTRAINT UK_competitor_name;
ALTER TABLE dinghy DROP CONSTRAINT FK_dinghy_dinghy_class_id;
ALTER TABLE dinghy_class DROP CONSTRAINT UK_dinghy_class_name;

DROP TABLE competitor_seq;
DROP TABLE dinghy_seq;
DROP TABLE dinghy_class_seq;
DROP TABLE lap_seq;
DROP TABLE entry_seq;
DROP TABLE race_seq;
DROP TABLE fleet_seq;
DROP TABLE signed_up_seq;

DROP TABLE embedded_race_hosts;
DROP TABLE competitor;
DROP TABLE dinghy;
DROP TABLE dinghy_class;
DROP TABLE signed_up;
DROP TABLE entry_laps;
DROP TABLE entry;
DROP TABLE lap;
DROP TABLE race;
DROP TABLE direct_race;
DROP TABLE embedded_race;
DROP TABLE fleet_dinghy_classes;
DROP TABLE fleet;