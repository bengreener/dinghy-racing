package com.bginfosys.dinghyracing.persistence;

import java.util.Set;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Race;

public class RaceRepositoryCustomImpl implements RaceRepositoryCustom {

	@Autowired
	private EntityManager entityManager;
	
	@Override
	@Transactional
	public Race save(Race race) {
		
		Assert.notNull(race, "Entity must not be null.");
		
		// check signed up dinghies are allowed for race
		Set<Entry> signedUp = race.getSignedUp();
		if (race.getSignedUp() != null && race.getDinghyClass() != null) {
			signedUp.stream().forEach(entry -> {
				if (entry.getDinghy().getDinghyClass() != race.getDinghyClass()) {
					throw new DinghyClassMismatchException();
				};
			});
		}
		
		if (!entityManager.contains(race) && race.getId() == null) {
			entityManager.persist(race);
			return race;
		}
		else {
			return entityManager.merge(race);
		}
		
	}

}
