-- v2025.9.2
use dinghy_racing;

update direct_race set planned_start_time = "2026-8-26 09:00:00" where id = 1;
update direct_race set planned_start_time = "2026-8-26 09:05:00" where id = 2;
update direct_race set planned_start_time = "2026-8-26 09:10:00" where id = 3;
update direct_race set planned_start_time = "2026-8-26 09:15:00" where id = 4;

update direct_race set planned_start_time = "2026-8-26 09:00:00" where id = 5;

update race set version = version + 1 where id = 1;
update race set version = version + 1 where id = 2;
update race set version = version + 1 where id = 3;
update race set version = version + 1 where id = 4;
update race set version = version + 1 where id = 7;