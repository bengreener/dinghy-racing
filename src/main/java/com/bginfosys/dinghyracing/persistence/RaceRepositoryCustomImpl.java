package com.bginfosys.dinghyracing.persistence;

import java.util.Set;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClassMismatchException;
import com.bginfosys.dinghyracing.model.Race;

public class RaceRepositoryCustomImpl implements RaceRepositoryCustom {

	@Autowired
	private EntityManager entityManager;
	
	@Override
	@Transactional
	public Race save(Race entity) {
		
		Assert.notNull(entity, "Entity must not be null.");
		
		// check signed up dinghies are allowed for race
		Set<Dinghy> signedUp = entity.getSignedUp();
		if (entity.getSignedUp() != null && entity.getDinghyClass() != null) {
			signedUp.stream().forEach(dinghy -> {
				if (dinghy.getDinghyClass() != entity.getDinghyClass()) {
					throw new DinghyClassMismatchException();
				};
			});
		}
		
		if (!entityManager.contains(entity) && entity.getId() == null) {
			entityManager.persist(entity);
			return entity;
		}
		else {
			return entityManager.merge(entity);
		}
		
	}

}
