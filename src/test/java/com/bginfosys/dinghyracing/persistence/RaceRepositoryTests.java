 package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.validation.ConstraintViolationException;

import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Competitor;

@DataJpaTest
public class RaceRepositoryTests {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	RaceRepository raceRepository;

	@Test
	void when_providedWithValidRace_then_savesRace() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
		
		Race race1 = new Race("Test Race", LocalDateTime.of(2023, 5, 13, 12, 00), dinghyClass, Duration.ofMinutes(45), 5);
		race1.setPlannedLaps(5);
		Race race2 = raceRepository.save(race1);
		
		assertThat(entityManager.find(Race.class, entityManager.getId(race2))).isEqualTo(race1);
	}
	
	@Test
	void when_raceHasNoName_then_throwsException() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
				
		Race race1 = new Race();
		race1.setPlannedStartTime(LocalDateTime.of(2023, 5, 13, 12, 00));
		race1.setDinghyClass(dinghyClass);
		race1.setDuration(Duration.ofMinutes(45));
		race1.setPlannedLaps(5);
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_raceHasNoPlannedStartTime_then_throwsException() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
				
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setDinghyClass(dinghyClass);
		race1.setPlannedLaps(5);
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_raceHasNoPlannedLaps_then_throwsException() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
				
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setPlannedStartTime(LocalDateTime.of(2023, 5, 13, 12, 00));
		race1.setDinghyClass(dinghyClass);
		race1.setDuration(Duration.ofMinutes(45));
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_raceHasNoDuration_then_throwsException() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
				
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setPlannedStartTime(LocalDateTime.of(2023, 5, 13, 12, 00));
		race1.setDinghyClass(dinghyClass);
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
		
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy);
				
		Race race1 = new Race("Test Race", LocalDateTime.of(2023, 5, 13, 12, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race1);
		// remove entity from session (detach entity). Not doing so can result in a false positive dependent on the logic used to check for an exisitng entity in repository save method
		entityManager.detach(race1);
		
		Entry entry = new Entry(competitor, dinghy, race1);
		entityManager.persist(entry);
		
		Set<Entry> signedUp = new HashSet<Entry>();
		signedUp.add(entry);
		race1.setSignedUp(signedUp);
		Race race2 = raceRepository.save(race1);
		
		assertThat(race1.getId() == race2.getId()
			&& race1.getName() == race2.getName()
			&& race1.getDinghyClass() == race2.getDinghyClass() 
			&& race1.getPlannedStartTime() == race2.getPlannedStartTime()
			&& race1.getSignedUp() == race2.getSignedUp()
		);
	}

	@Test
	void when_aCollectionOfRacesAfterACertainTimeIsRequested_then_ACollectionContainingOnlyRacesThatStartAtOrAfterThatTimeIsReturned() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
		
		Race race1 = new Race("Test Race1", LocalDateTime.of(2023, 5, 13, 11, 00), dinghyClass, Duration.ofMinutes(45), 5);
		race1 = entityManager.persist(race1);
		Race race2 = new Race("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00), dinghyClass, Duration.ofMinutes(45), 5);
		race2 = entityManager.persist(race2);
		Race race3 = new Race("Test Race3", LocalDateTime.of(2023, 5, 13, 13, 00), dinghyClass, Duration.ofMinutes(45), 5);
		race3 = entityManager.persist(race3);
		
		List<Race> result = raceRepository.findByPlannedStartTimeGreaterThanEqual(LocalDateTime.of(2023, 5, 13, 12, 00));
		
		assertThat(result).contains(race2, race3);
	}
	
	@Test
	void when_raceIsStarted_then_savesRaceWIthActualStartTime() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy);
				
		Race race1 = new Race("Test Race", LocalDateTime.of(2023, 5, 13, 12, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race1);
		// remove entity from session (detach entity). Not doing so can result in a false positive dependent on the logic used to check for an exisitng entity in repository save method
		entityManager.detach(race1);
		
		LocalDateTime startTime = LocalDateTime.now();
		race1.setActualStartTime(startTime);
		Race race2 = raceRepository.save(race1);
		
		assertThat(race1.getActualStartTime() == race2.getActualStartTime());
	}
	
	@Test
	void when_raceIsRequestedByNameAndPlannedStartTime_then_raceIsReturned() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
		
		Race race1 = new Race("Test Race1", LocalDateTime.of(2023, 5, 13, 11, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race1);
		Race race2 = new Race("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race2);
		Race race3 = new Race("Test Race3", LocalDateTime.of(2023, 5, 13, 13, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race3);
		entityManager.flush();
		
		Race result = raceRepository.findByNameAndPlannedStartTime("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00));
		
		assertEquals(race2, result);
	}

	@Test
	void given_raceExistsWithNameAndStartTime_when_creatingAnotherRaceWithTheSameNameAndStartTime_then_throwsError() {
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setPlannedStartTime(LocalDateTime.of(2023, 10, 13, 12, 00));
		race1.setDuration(Duration.ofMinutes(45));
		race1.setPlannedLaps(5);
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
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		entityManager.persist(dinghyClass);
		
		Race race1 = new Race("Test Race1", LocalDateTime.of(2023, 5, 13, 11, 59), dinghyClass, Duration.ofMinutes(45), 5);
		race1 = entityManager.persist(race1);
		Race race2 = new Race("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00), dinghyClass, Duration.ofMinutes(45), 5);
		race2 = entityManager.persist(race2);
		Race race3 = new Race("Test Race3", LocalDateTime.of(2023, 5, 13, 13, 00), dinghyClass, Duration.ofMinutes(45), 5);
		race3 = entityManager.persist(race3);
		Race race4 = new Race("Test Race4", LocalDateTime.of(2023, 5, 13, 13, 01), dinghyClass, Duration.ofMinutes(45), 5);
		race4 = entityManager.persist(race4);
		
		List<Race> result = raceRepository.findByPlannedStartTimeBetween(LocalDateTime.of(2023, 5, 13, 12, 00), 
				LocalDateTime.of(2023, 5, 13, 13, 00));
		
		assertThat(result).contains(race2, race3);
		assertThat(result).doesNotContain(race1, race4);
	}

}
