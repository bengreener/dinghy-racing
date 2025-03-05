package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.bginfosys.dinghyracing.model.EmbeddedRace;

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
}
