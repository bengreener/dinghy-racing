package com.bginfosys.dinghyracing;

import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.persistence.DinghyClassRepository;
import com.bginfosys.dinghyracing.persistence.DinghyRepository;
import com.bginfosys.dinghyracing.persistence.RaceRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final RaceRepository races;
	private final DinghyClassRepository dinghyClasses;
	private final DinghyRepository dinghies;

	@Autowired
	public DatabaseLoader(RaceRepository raceRepository, DinghyClassRepository dinghyClasses, DinghyRepository dinghies) {
		this.races = raceRepository;
		this.dinghyClasses = dinghyClasses;
		this.dinghies = dinghies;
	}
	
	@Override
	public void run(String... args) throws Exception {
		DinghyClass dc = new DinghyClass("Scorpion");
		Dinghy d1 = new Dinghy("1234", dc);
		Dinghy d2 = new Dinghy("6745", dc);
		Race r = new Race("Scorpion A", LocalDateTime.of(2021, 10, 14, 14, 10), dc);
		
		this.dinghyClasses.save(dc);
		
		this.dinghies.save(d1);
		this.dinghies.save(d2);
		
		DinghyClass dc2 = new DinghyClass("Graduate");
		Dinghy d3 = new Dinghy("2726", dc2);
		this.dinghyClasses.save(dc2);
		this.dinghies.save(d3);
		
		//this.races.save(new Race("Scorpion A", LocalDate.of(2021, 10, 14), LocalTime.of(14, 10), dc));
		//this.races.save(new Race("Scorpion A", LocalDateTime.of(2021, 10, 14, 14, 10), dc));
		//this.races.save(new Race("Test", LocalDate.of(2022, 10, 10), LocalTime.of(15, 35)));
		this.races.save(r);
		
		r.signUpDinghy(d1);
		this.races.save(r);
	}

}
