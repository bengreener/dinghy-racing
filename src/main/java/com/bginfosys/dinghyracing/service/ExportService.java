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
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.hateoas.UriTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bginfosys.dinghyracing.exceptions.RaceNotFoundException;
import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.RaceResult;
import com.bginfosys.dinghyracing.model.RaceType;
import com.bginfosys.dinghyracing.persistence.RaceRepository;

@Service
public class ExportService {

	private Logger logger = LoggerFactory.getLogger(DropboxService.class);
	
	private final RaceRepository raceRepository;
	
	private final PersistentEntities persistentEntities;
	
	private final RepositoryInvokerFactory repositoryInvokerFactory;
	
	private final ConversionService conversionService;
	
	public ExportService(RaceRepository raceRepository, PersistentEntities persistentEntities, RepositoryInvokerFactory repositoryInvokerFactory,
			@Qualifier("mvcConversionService") ConversionService conversionService) {
		this.raceRepository = raceRepository;
		this.persistentEntities = persistentEntities;
		this.repositoryInvokerFactory = repositoryInvokerFactory;
		this.conversionService = conversionService;
	}
	
	@Transactional
	public void generateResultsFile(Long raceId) {
		Optional<Race> optRace = raceRepository.findById(raceId);
		if (optRace.isEmpty()) {
			throw new RaceNotFoundException(raceId);
		}
		generateResultsFile((RaceResult) optRace.get());
	}
	
	@Transactional
	public void generateResultsFile(String raceUri) {
		TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(Race.class);
		RaceResult raceResult = (RaceResult) getEntityFromUri(UriTemplate.of(raceUri).expand(), typeDescriptor);
		if (raceResult != null) {
			generateResultsFile(raceResult);	
		}
		else {
			throw new RaceNotFoundException(raceUri);
		}
	}

	private void generateResultsFile(RaceResult raceResult) {
		String filename = raceResult.getName() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
		Path filepath = FileSystems.getDefault().getPath("results", filename);
		if(!Files.exists(filepath.getParent())) {
			try {
				Files.createDirectory(filepath.getParent());	
			}
			catch (IOException exception) {
				logger.error("ExportService.generateResultsFile" + exception.getMessage());
				throw new ExportException(exception.getMessage(), exception);
			}
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
					position = (s.getPosition() != null) ? s.getPosition().toString() : "";
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
		catch (IOException exception) {
			logger.error("ExportService.generateResultsFile" + exception.getMessage());
			throw new ExportException(exception.getMessage(), exception);
		}
	}

	/**
	 * Removes all files from the results directory
	 */
	public void deleteAllResultsFiles() {
		try (Stream<Path> paths = Files.list(Paths.get("results"))) {
			paths.forEach(p -> {
				try {
					Files.delete(p);
				} catch (IOException exception) {
					logger.error("ExportService.deleteAllResultsFiles" + exception.getMessage());
					throw new ExportException(exception.getMessage(), exception);
				}	
			});
		} catch (IOException exception) {
			logger.error("ExportService.deleteAllResultsFiles" + exception.getMessage());
			throw new ExportException(exception.getMessage(), exception);
		}
	}

	private Object getEntityFromUri(URI uri, TypeDescriptor targetType) {
		TypeDescriptor sourceType = TypeDescriptor.valueOf(URI.class);

		UriToEntityConverter uriToEntityConverter = new UriToEntityConverter(persistentEntities, repositoryInvokerFactory, () -> conversionService);

		return uriToEntityConverter.convert(uri, sourceType, targetType);
	}
}
