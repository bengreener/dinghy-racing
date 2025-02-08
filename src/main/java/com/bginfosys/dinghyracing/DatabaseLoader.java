/*
 * Copyright 2022-2024 BG Information Systems Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
   
package com.bginfosys.dinghyracing;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.Competitor;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.RaceType;
import com.bginfosys.dinghyracing.model.StartType;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Fleet;
import com.bginfosys.dinghyracing.persistence.CompetitorRepository;
import com.bginfosys.dinghyracing.persistence.DinghyClassRepository;
import com.bginfosys.dinghyracing.persistence.DinghyRepository;
import com.bginfosys.dinghyracing.persistence.EntryRepository;
import com.bginfosys.dinghyracing.persistence.FleetRepository;
import com.bginfosys.dinghyracing.persistence.RaceRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Profile("dev")
@Component
public class DatabaseLoader implements CommandLineRunner {

	private final RaceRepository raceRepository;
	private final CompetitorRepository competitorRepository;
	private final DinghyClassRepository dinghyClassRepository;
	private final DinghyRepository dinghyRepository;
	private final EntryRepository entryRepository;
	private final FleetRepository fleetRepository;

	public DatabaseLoader(RaceRepository raceRepository, CompetitorRepository competitorRepository, DinghyClassRepository dinghyClassRepository, DinghyRepository dinghyRepository, EntryRepository entryRepository, FleetRepository fleetRepository) {
		this.raceRepository = raceRepository;
		this.competitorRepository = competitorRepository;
		this.dinghyClassRepository = dinghyClassRepository;
		this.dinghyRepository = dinghyRepository;
		this.entryRepository = entryRepository;
		this.fleetRepository = fleetRepository;
	}
	
	@Override
	public void run(String... args) throws Exception {
		DinghyClass dcScorpion = new DinghyClass("Scorpion", 2);
		DinghyClass dcGraduate = new DinghyClass("Graduate", 2);
		DinghyClass dcComet = new DinghyClass("Comet", 1);
		
		this.dinghyClassRepository.save(dcScorpion);
		this.dinghyClassRepository.save(dcGraduate);
		this.dinghyClassRepository.save(dcComet);
		
		Dinghy d1234 = new Dinghy("1234", dcScorpion);
		Dinghy d6745 = new Dinghy("6745", dcScorpion);
		Dinghy d2726 = new Dinghy("2726", dcGraduate);
		Dinghy d826 = new Dinghy("826", dcComet);
		
		this.dinghyRepository.save(d1234);
		this.dinghyRepository.save(d6745);
		this.dinghyRepository.save(d2726);
		this.dinghyRepository.save(d826);
		
		Set<DinghyClass> dcsScorpion = new HashSet<DinghyClass>();
		dcsScorpion.add(dcScorpion);
		Fleet fScorpion = new Fleet("Scorpion", dcsScorpion);
		Set<DinghyClass> dcsGraduate = new HashSet<DinghyClass>();
		dcsGraduate.add(dcGraduate);
		Fleet fGraduate = new Fleet("Graduate", dcsGraduate);
		Set<DinghyClass> dcsComet = new HashSet<DinghyClass>();
		Fleet fComet = new Fleet("Comet", dcsComet);
		Fleet fHandicap = new Fleet("Handicap");

		this.fleetRepository.save(fScorpion);
		this.fleetRepository.save(fGraduate);
		this.fleetRepository.save(fComet);
		this.fleetRepository.save(fHandicap);
		
		LocalDateTime now = LocalDateTime.now();
		now = now.minusNanos(now.getNano()); // avoid precision issues saving and retrieving from database
		
		Race rScorpionA = new Race("Scorpion A", now.plusMinutes(0L), fScorpion, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Race rGraduateA = new Race("Graduate A", now.plusMinutes(6L), fGraduate, Duration.ofMinutes(35), 4, RaceType.FLEET, StartType.CSCCLUBSTART);
		Race rCometA = new Race("Comet A", now.plusMinutes(11L), fComet, Duration.ofMinutes(35), 4, RaceType.FLEET, StartType.CSCCLUBSTART);
		Race rHandicapA = new Race("Handicap A", now.plusMinutes(1L), fHandicap, Duration.ofMinutes(35), 4, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		this.raceRepository.save(rScorpionA);
		this.raceRepository.save(rGraduateA);
		this.raceRepository.save(rCometA);
		this.raceRepository.save(rHandicapA);

		Competitor helm1 = new Competitor("Chris Marshall");
		Competitor helm2 = new Competitor("Sarah Pascal");
		Competitor helm3 = new Competitor("Jill Myer");
		Competitor crew1 = new Competitor("Lou Screw");
		Competitor crew2 = new Competitor("Owain Davies");
		Competitor crew3 = new Competitor("Liu Bao");
		
		competitorRepository.save(helm1);
		competitorRepository.save(helm2);
		competitorRepository.save(helm3);
		competitorRepository.save(crew1);
		competitorRepository.save(crew2);
		competitorRepository.save(crew3);
		
		Entry e1 = new Entry(helm1, d1234, rScorpionA);
		e1.setCrew(crew1);
		Entry e2 = new Entry(helm2, d6745, rScorpionA);
		e2.setCrew(crew2);
		Entry e3 = new Entry(helm3, d826, rCometA);
		e3.setCrew(crew3);
		
		entryRepository.save(e1);
		entryRepository.save(e2);
		entryRepository.save(e3);
		
		rScorpionA.signUp(e1);
		rScorpionA.signUp(e2);
		this.raceRepository.save(rScorpionA);
		
		rCometA.signUp(e3);
		this.raceRepository.save(rCometA);
	}
}
