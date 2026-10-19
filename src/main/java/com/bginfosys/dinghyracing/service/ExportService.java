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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.bginfosys.dinghyracing.model.RaceResult;
import com.bginfosys.dinghyracing.model.RaceType;

@Service
public class ExportService {

	public void generateResultsFile(RaceResult raceResult) throws IOException {
		String filename = raceResult.getName() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
		Path filepath = FileSystems.getDefault().getPath("results", filename);
		if(!Files.exists(filepath.getParent())) {
			Files.createDirectory(filepath.getParent());
		}
		Boolean singleHanded = raceResult.singleHandedOnly();
		
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filepath, StandardCharsets.UTF_8))) {
			switch (raceResult.getType()) {
				case RaceType.PURSUIT:
					if (singleHanded) {
						writer.println("HelmName,SailNo,Class,Place,Code,RaceRating");
					}
					else {
						writer.println("HelmName,CrewName,SailNo,Class,Place,Code,RaceRating");
					}
					break;
				default:
					if (raceResult.singleHandedOnly()) {
						writer.println("HelmName,SailNo,Class,Place,Elapsed,Laps,Code,RaceRating,Corrected");
					}
					else {
						writer.println("HelmName,CrewName,SailNo,Class,Place,Elapsed,Laps,Code,RaceRating,Corrected");
					}
					break;
			}
			raceResult.getSignedUp().forEach(s -> {
				String record = "";
				// helm name
				record += s.getEntry().getHelm().getName() + ",";
				// crew name
				if (!singleHanded) {
					if (s.getEntry().getCrew() != null) {
						record += s.getEntry().getCrew().getName() + ",";	
					}
					else {
						record += ",";
					}
				}
				// sail number
				record += s.getEntry().getDinghy().getSailNumber() + ",";
				// dinghy class name
				String dinghyClassName = s.getEntry().getDinghy().getDinghyClass().getExternalName();
				if (dinghyClassName == null) {
					dinghyClassName = s.getEntry().getDinghy().getDinghyClass().getName();
				}
				record += dinghyClassName + ",";
				// position/ place
				String position = "";
				if (s.getEntry().getScoringAbbreviation() == "") {
					position = s.getPosition().toString();
				}
				record += position + ",";
				// elapsed time to finish and number of laps sailed
				if (raceResult.getType() != RaceType.PURSUIT) {
					Long elapsedSeconds = Long.valueOf(s.getEntry().getSumOfLapTimes().getSeconds());
					record += elapsedSeconds > 0 ? elapsedSeconds.toString() + "," : ",";
					record += s.getEntry().getLapsSailed().toString() + ",";
				}
				// scoring abbreviation
				String scoringAbbreviation = s.getEntry().getScoringAbbreviation();
				if (scoringAbbreviation == null) {
					scoringAbbreviation = "";
				}
				record += scoringAbbreviation + ",";
				// portsmouth number
				Integer portsmouthNumber = s.getEntry().getDinghy().getDinghyClass().getPortsmouthNumber();
				if (portsmouthNumber == null) {
					record += ","; 
				}
				else {
					record += portsmouthNumber.toString() + ",";
				}
				// corrected time
				if (raceResult.getType() != RaceType.PURSUIT) {
					record += s.getCorrectedTime().getSeconds() == (long) Double.POSITIVE_INFINITY ? "" : String.valueOf(s.getCorrectedTime().getSeconds());
				}
				writer.println(record);
			});	
		}
		
	}

}
