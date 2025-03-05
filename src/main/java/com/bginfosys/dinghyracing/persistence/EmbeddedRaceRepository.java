package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bginfosys.dinghyracing.model.EmbeddedRace;

public interface EmbeddedRaceRepository extends JpaRepository<EmbeddedRace, Long> {

}
