/*
 * Copyright 2022-2024 BG Information Systems Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.bginfosys.dinghyracing.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.persistence.RaceRepository;

@Service
@ConditionalOnProperty(value= {"dropbox-export-schedule"})
public class ScheduleService {

	private final RaceRepository raceRepository;
	
	private final ExportService exportService;
	
	private final DropboxService dropboxService;
	
	public ScheduleService(RaceRepository raceRepository, ExportService exportService, DropboxService dropboxService) {
		this.raceRepository = raceRepository;
		this.exportService = exportService;
		this.dropboxService = dropboxService;
	}
	
	@Scheduled(cron = "${dropbox-export-schedule}")
	public void exportTodaysRaceResults() {
		List<Race> races = raceRepository.findByPlannedStartTimeBetweenAsList(LocalDateTime.of(LocalDate.now(), LocalTime.MIN), LocalDateTime.of(LocalDate.now(), LocalTime.MAX));
		races.forEach(race -> {
			// Races in returns from RaceRepository.findByPlannedStartTimeBetweenAsList() do not support polymorphism so unable to cast to RaceResult
			// Also results in cast exceptions if this is moved into the same transaction context as methods in ExportService; which is why this is done here and not there
			exportService.generateResultsFile(race.getId());
		});
		dropboxService.transferResultsFilesToDropbox();
		exportService.deleteAllResultsFiles(); 
	}
}
