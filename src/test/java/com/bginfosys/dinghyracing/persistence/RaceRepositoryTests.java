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
   
package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.ConstraintViolationException;

import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.RaceType;
import com.bginfosys.dinghyracing.model.StartType;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Fleet;
import com.bginfosys.dinghyracing.model.Competitor;

@DataJpaTest
public class RaceRepositoryTests {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	RaceRepository raceRepository;

	@Test
	void when_providedWithValidRace_then_savesRace() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
		
		Race race1 = new Race("Test Race", LocalDateTime.of(2023, 5, 13, 12, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race1.setPlannedLaps(5);
		Race race2 = raceRepository.save(race1);
		
		assertThat(entityManager.find(Race.class, entityManager.getId(race2))).isEqualTo(race1);
	}
	
	@Test
	void when_raceHasNoName_then_throwsException() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
				
		Race race1 = new Race();
		race1.setPlannedStartTime(LocalDateTime.of(2023, 5, 13, 12, 00));
		race1.setFleet(fleet);
		race1.setDuration(Duration.ofMinutes(45));
		race1.setPlannedLaps(5);
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_raceHasNoPlannedStartTime_then_throwsException() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
				
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setFleet(fleet);
		race1.setPlannedLaps(5);
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_raceHasNoPlannedLaps_then_throwsException() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
				
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setPlannedStartTime(LocalDateTime.of(2023, 5, 13, 12, 00));
		race1.setFleet(fleet);
		race1.setDuration(Duration.ofMinutes(45));
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_raceHasNoFleet_then_throwsException() {
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setPlannedStartTime(LocalDateTime.of(2023, 5, 13, 12, 00));
		race1.setDuration(Duration.ofMinutes(45));
		race1.setPlannedLaps(5);
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_raceHasNoDuration_then_throwsException() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
				
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setPlannedStartTime(LocalDateTime.of(2023, 5, 13, 12, 00));
		race1.setFleet(fleet);
		race1.setPlannedLaps(5);
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void given_raceAlreadySaved_when_raceUpdated_updatedVersionIsSaved() {
		Competitor competitor = new Competitor();
		entityManager.persist(competitor);
		
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass", 1, 1000);
		entityManager.persist(dinghyClass);
		
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
		
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy);
				
		Race race1 = new Race("Test Race", LocalDateTime.of(2023, 5, 13, 12, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		entityManager.persist(race1);
		// remove entity from session (detach entity). Not doing so can result in a false positive dependent on the logic used to check for an existing entity in repository save method
		entityManager.detach(race1);
		
		Entry entry = new Entry(competitor, dinghy, race1);
		entityManager.persist(entry);
		
		Set<Entry> signedUp = new HashSet<Entry>();
		signedUp.add(entry);
		race1.setSignedUp(signedUp);
		Race race2 = raceRepository.save(race1);
		
		assertThat(race1.getId() == race2.getId()
			&& race1.getName() == race2.getName()
			&& race1.getFleet() == race2.getFleet() 
			&& race1.getPlannedStartTime() == race2.getPlannedStartTime()
			&& race1.getSignedUp() == race2.getSignedUp()
		);
	}

	@Test
	void when_aCollectionOfRacesAfterACertainTimeIsRequested_then_ACollectionContainingOnlyRacesThatStartAtOrAfterThatTimeIsReturned() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
		
		Race race1 = new Race("Test Race1", LocalDateTime.of(2023, 5, 13, 11, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race1 = entityManager.persist(race1);
		Race race2 = new Race("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race2 = entityManager.persist(race2);
		Race race3 = new Race("Test Race3", LocalDateTime.of(2023, 5, 13, 13, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race3 = entityManager.persist(race3);
		
		Page<Race> result = raceRepository.findByPlannedStartTimeGreaterThanEqual(LocalDateTime.of(2023, 5, 13, 12, 00), Pageable.ofSize(5));
		
		assertThat(result).contains(race2, race3);
	}
	
	@Test
	void when_raceIsRequestedByNameAndPlannedStartTime_then_raceIsReturned() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
		
		Race race1 = new Race("Test Race1", LocalDateTime.of(2023, 5, 13, 11, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		entityManager.persist(race1);
		Race race2 = new Race("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		entityManager.persist(race2);
		Race race3 = new Race("Test Race3", LocalDateTime.of(2023, 5, 13, 13, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		entityManager.persist(race3);
		entityManager.flush();
		
		Race result = raceRepository.findByNameAndPlannedStartTime("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00));
		
		assertEquals(race2, result);
	}

	@Test
	void given_raceExistsWithNameAndStartTime_when_creatingAnotherRaceWithTheSameNameAndStartTime_then_throwsError() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
				
		Race race1 = new Race("Test Race", LocalDateTime.of(2023, 10, 13, 12, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		entityManager.persistAndFlush(race1);
		
		Race race2 = new Race();
		race2.setName("Test Race");
		race2.setPlannedStartTime(LocalDateTime.of(2023, 10, 13, 12, 00));
		race2.setDuration(Duration.ofMinutes(45));
		race1.setPlannedLaps(5);
		
		assertThrows(ConstraintViolationException.class, () -> {
			raceRepository.save(race2);
			entityManager.flush();
		});
	}

	@Test
	void when_aCollectionOfRacesBetweenCertainTimesIsRequested_then_ACollectionContainingOnlyRacesBetweenThomseTimesAreReturned() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
		
		Race race1 = new Race("Test Race1", LocalDateTime.of(2023, 5, 13, 11, 59), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race1 = entityManager.persist(race1);
		Race race2 = new Race("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race2 = entityManager.persist(race2);
		Race race3 = new Race("Test Race3", LocalDateTime.of(2023, 5, 13, 13, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race3 = entityManager.persist(race3);
		Race race4 = new Race("Test Race4", LocalDateTime.of(2023, 5, 13, 13, 01), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race4 = entityManager.persist(race4);
		
		Page<Race> result = raceRepository.findByPlannedStartTimeBetween(LocalDateTime.of(2023, 5, 13, 12, 00), 
				LocalDateTime.of(2023, 5, 13, 13, 00), Pageable.ofSize(5));
		
		assertThat(result).contains(race2, race3);
		assertThat(result).doesNotContain(race1, race4);
	}
	
	@Test
	void when_aCollectionOfRacesBetweenCertainTimesAndTypeEqualsIsRequested_then_ACollectionContainingOnlyRacesBetweenThoseTimesWithThatTypeAreReturned() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
		
		Race race1 = new Race("Test Race1", LocalDateTime.of(2023, 5, 13, 11, 59), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race1 = entityManager.persist(race1);
		Race race2 = new Race("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race2 = entityManager.persist(race2);
		Race race3 = new Race("Test Race3", LocalDateTime.of(2023, 5, 13, 13, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race3 = entityManager.persist(race3);
		Race race4 = new Race("Test Race4", LocalDateTime.of(2023, 5, 13, 13, 01), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race4 = entityManager.persist(race4);
		Race race5 = new Race("Test Race5", LocalDateTime.of(2023, 5, 13, 12, 00), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		race5 = entityManager.persist(race5);
		Race race6 = new Race("Test Race6", LocalDateTime.of(2023, 5, 13, 13, 00), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		race6 = entityManager.persist(race6);
		
		Page<Race> result = raceRepository.findByPlannedStartTimeBetweenAndTypeEquals(LocalDateTime.of(2023, 5, 13, 12, 00), 
				LocalDateTime.of(2023, 5, 13, 13, 00), RaceType.FLEET, Pageable.ofSize(5));
		
		assertThat(result).contains(race2, race3);
		assertThat(result).doesNotContain(race1, race4);
	}
}
