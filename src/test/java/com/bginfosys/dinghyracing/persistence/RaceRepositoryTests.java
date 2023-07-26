package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;
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
	
	@Autowired
	DinghyClassRepository dinghyClassRepository;
	
	@Autowired
	DinghyRepository dinghyRepository;
	
	@Autowired
	CompetitorRepository competitorRepository;
	
	@Autowired
	EntryRepository entryRepository;

	@Test
	void when_providedWithValidRace_then_savesRace() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		dinghyClassRepository.save(dinghyClass);
		
		long raceCount = raceRepository.count();
		
		Race race1 = new Race("Test Race", LocalDateTime.of(2023, 5, 13, 12, 00), dinghyClass);
		Race race2 = raceRepository.save(race1);
		
		assertThat(raceRepository.count() == raceCount + 1 && (race1.getName() == race2.getName()
			&& race1.getDinghyClass() == race2.getDinghyClass() 
			&& race1.getPlannedStartTime() == race2.getPlannedStartTime()
		));
	}
	
	@Test
	void when_raceHasNoName_then_throwsException() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		dinghyClassRepository.save(dinghyClass);
				
		Race race1 = new Race();
		race1.setPlannedStartTime(LocalDateTime.of(2023, 5, 13, 12, 00));
		race1.setDinghyClass(dinghyClass);
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			// force flush of memory to database (could probably call entityManager.flush() instead now :-))
			raceRepository.count();
		});
	}
	
	@Test
	void when_raceHasNoPlannedStartTime_then_throwsException() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		dinghyClassRepository.save(dinghyClass);
				
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setDinghyClass(dinghyClass);
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			// force flush of memory to database
			raceRepository.count();
		});
	}
	
	@Test
	void given_raceAlreadySaved_when_raceUpdated_updatedVersionIsSaved() {
		Competitor competitor = new Competitor();
		competitorRepository.save(competitor);
		
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		dinghyClassRepository.save(dinghyClass);
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		dinghyRepository.save(dinghy);
				
		Race race1 = new Race("Test Race", LocalDateTime.of(2023, 5, 13, 12, 00), dinghyClass);
		entityManager.persist(race1);
		// remove entity from session (detach entity). Not doing so can result in a false positive dependent on the logic used to check for an exisitng entity in repository save method
		entityManager.detach(race1);
		
		Entry entry = new Entry(competitor, dinghy, race1);
		entryRepository.save(entry);
		
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

	void when_aCollectionOfRacesAfterACertainTimeIsRequested_then_ACollectionContainingOnlyRacesThatStartAtOrAfterThatTimeIsReturned() {
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		
		Race race1 = new Race("Test Race1", LocalDateTime.of(2023, 5, 13, 11, 00), dinghyClass);
		raceRepository.save(race1);
		Race race2 = new Race("Test Race2", LocalDateTime.of(2023, 5, 13, 12, 00), dinghyClass);
		raceRepository.save(race2);
		Race race3 = new Race("Test Race3", LocalDateTime.of(2023, 5, 13, 13, 00), dinghyClass);
		raceRepository.save(race3);
		
		List<Race> result = raceRepository.findByPlannedStartTimeGreaterThanEqual(LocalDateTime.of(2023, 5, 13, 12, 00));
		
		assertThat(result).contains(race2, race3);
	}
}
