package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bginfosys.dinghyracing.model.Race;

public interface RaceRepository extends JpaRepository<Race, Long> {

}
