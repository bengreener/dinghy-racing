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

import java.io.IOException;
import java.util.Optional;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bginfosys.dinghyracing.exceptions.RaceNotFoundException;
import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.RaceResult;
import com.bginfosys.dinghyracing.persistence.RaceRepository;
import com.bginfosys.dinghyracing.service.ExportService;

@RepositoryRestController
public class RaceController {

	private final RaceRepository raceRepository;
	
	private final ExportService exportService;
	
	public RaceController(RaceRepository raceRepository, ExportService exportService) {
		this.raceRepository = raceRepository;
		this.exportService = exportService;
	}
	
	@GetMapping("/races/{id}/createRaceResultFile")
	public ResponseEntity<Object> createRaceResultFile(@PathVariable("id") Long id) {
		Optional<Race> optRace = raceRepository.findById(id);
		if (optRace.isEmpty()) {
			throw new RaceNotFoundException(id);
		}
		try {
			exportService.generateResultsFile((RaceResult) optRace.get());
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
		return ResponseEntity.ok().build();
	}
}
