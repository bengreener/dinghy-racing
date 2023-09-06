package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.repository.CrudRepository;

import com.bginfosys.dinghyracing.model.Lap;

public interface LapRepository extends CrudRepository<Lap, Long> {

}
