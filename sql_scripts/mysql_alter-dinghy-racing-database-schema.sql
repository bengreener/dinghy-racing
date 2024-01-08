USE dinghy_racing;

ALTER TABLE entry DROP CONSTRAINT FK_entry_competitor_id;
ALTER TABLE entry DROP CONSTRAINT UK_entry_competitor_id_dinghy_id_race_id;
ALTER TABLE entry DROP CONSTRAINT UK_entry_competitor_id_race_id;

ALTER TABLE dinghy_class ADD COLUMN crew_size TINYINT NOT NULL;
ALTER TABLE entry RENAME COLUMN competitor_id TO helm_id;
ALTER TABLE entry ADD COLUMN crew_id BIGINT NULL;

ALTER TABLE entry ADD CONSTRAINT UK_entry_helm_id_dinghy_id_race_id UNIQUE (helm_id, dinghy_id, race_id);
ALTER TABLE entry ADD CONSTRAINT UK_entry_helm_id_race_id UNIQUE (helm_id, race_id);
ALTER TABLE entry ADD CONSTRAINT FK_entry_helm_id FOREIGN KEY (helm_id) REFERENCES competitor (id);
ALTER TABLE entry ADD CONSTRAINT UK_entry_crew_id_race_id UNIQUE (crew_id, race_id);
ALTER TABLE entry ADD CONSTRAINT FK_entry_crew_id FOREIGN KEY (crew_id) REFERENCES competitor (id);