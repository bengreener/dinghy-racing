package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.bginfosys.dinghyracing.model.DirectRace;
import com.bginfosys.dinghyracing.model.EmbeddedRace;
import com.bginfosys.dinghyracing.model.Fleet;
import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.RaceType;
import com.bginfosys.dinghyracing.model.StartType;

@DataJpaTest
public class EmbeddedRaceRepositoryTests {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	EmbeddedRaceRepository embeddedRaceRepository;
	
	@Test
	void given_embeddedRaceExists_then_savesEmbeddedRace() {
		EmbeddedRace er1 = new EmbeddedRace();
		
		EmbeddedRace er2 = embeddedRaceRepository.save(er1);
				
		assertThat(entityManager.find(EmbeddedRace.class, entityManager.getId(er2))).isEqualTo(er1);
	}
	
	@Test
	void given_embededRaceHasHostsSet_then_savesEmbeddedRace() {
		Fleet fleet1 = new Fleet("Fleet1");
		entityManager.persist(fleet1);
		
		DirectRace dr1 = new DirectRace("DR1", LocalDateTime.now(), fleet1, Duration.ofHours(1), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		entityManager.persist(dr1);
		
		EmbeddedRace er1 = new EmbeddedRace();
		Set<Race> hosts = new HashSet<Race>();
		hosts.add(dr1);
		er1.setHosts(hosts);
		EmbeddedRace er2 = embeddedRaceRepository.save(er1);
				
		assertThat(entityManager.find(EmbeddedRace.class, entityManager.getId(er2))).isEqualTo(er1);
	}
}
