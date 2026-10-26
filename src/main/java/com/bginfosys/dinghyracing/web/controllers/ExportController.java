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

package com.bginfosys.dinghyracing.web.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.persistence.RaceRepository;
import com.bginfosys.dinghyracing.service.ExportService;

@BasePathAwareController
public class ExportController {

	private final ExportService exportService;
	
	private final RaceRepository raceRepository;
	
	public ExportController(ExportService exportService, RaceRepository raceRepository) {
		this.exportService = exportService;
		this.raceRepository = raceRepository;
	}

	/**
	 * Export race results for the selected race to the servers results folder
	 * @param race URI of the race
	 * @return
	 */
	@PostMapping("/exports/createRaceResultFile")
	public ResponseEntity<Object> createRaceResultFile(@RequestParam("race") String race) {
		exportService.generateResultsFile(race);
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * Export race results files for all races on the selected day
	 * @param date for which to export race results
	 * @return
	 */
	@PostMapping("/exports/createRaceResultsFiles")
	public ResponseEntity<Object> createRaceResultsFiles(@RequestParam("date") LocalDate date) {
		List<Race> races = raceRepository.findByPlannedStartTimeBetweenAsList(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
		races.forEach(race -> {
			// Races in returns from RaceRepository.findByPlannedStartTimeBetweenAsList() do not support polymorphism so unable to cast to RaceResult
			// Also results in cast exceptions if this is moved into the same transaction context as methods in ExportService; which is why this is done here and not there
			exportService.generateResultsFile(race.getId());
		});
		return ResponseEntity.ok().build();	
	}
}
