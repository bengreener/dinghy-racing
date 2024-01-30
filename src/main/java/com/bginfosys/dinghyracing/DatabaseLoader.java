package com.bginfosys.dinghyracing;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.Competitor;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.persistence.CompetitorRepository;
import com.bginfosys.dinghyracing.persistence.DinghyClassRepository;
import com.bginfosys.dinghyracing.persistence.DinghyRepository;
import com.bginfosys.dinghyracing.persistence.EntryRepository;
import com.bginfosys.dinghyracing.persistence.RaceRepository;

import java.time.Duration;
import java.time.LocalDateTime;

@Profile("dev")
@Component
public class DatabaseLoader implements CommandLineRunner {

	private final RaceRepository raceRepository;
	private final CompetitorRepository competitorRepository;
	private final DinghyClassRepository dinghyClassRepository;
	private final DinghyRepository dinghyRepository;
	private final EntryRepository entryRepository;

	public DatabaseLoader(RaceRepository raceRepository, CompetitorRepository competitorRepository, DinghyClassRepository dinghyClassRepository, DinghyRepository dinghyRepository, EntryRepository entryRepository) {
		this.raceRepository = raceRepository;
		this.competitorRepository = competitorRepository;
		this.dinghyClassRepository = dinghyClassRepository;
		this.dinghyRepository = dinghyRepository;
		this.entryRepository = entryRepository;
	}
	
	@Override
	public void run(String... args) throws Exception {
		DinghyClass dc1 = new DinghyClass("Scorpion", 2);
		DinghyClass dc2 = new DinghyClass("Graduate", 2);
		DinghyClass dc3 = new DinghyClass("Comet", 1);
		
		this.dinghyClassRepository.save(dc1);
		this.dinghyClassRepository.save(dc2);
		this.dinghyClassRepository.save(dc3);
		
		Dinghy d1 = new Dinghy("1234", dc1);
		Dinghy d2 = new Dinghy("6745", dc1);
		Dinghy d3 = new Dinghy("2726", dc2);
		Dinghy d4 = new Dinghy("826", dc3);
		
		this.dinghyRepository.save(d1);
		this.dinghyRepository.save(d2);
		this.dinghyRepository.save(d3);
		this.dinghyRepository.save(d4);
		
		LocalDateTime now = LocalDateTime.now();
		now = now.minusNanos(now.getNano()); // avoid precision issues saving and retrieving from database
		
//		Race r1 = new Race("Scorpion A", now.plusMinutes(2L), dc1, Duration.ofMinutes(45), 5);
		Race r1 = new Race("Scorpion A", now.plusMinutes(0L), dc1, Duration.ofMinutes(2), 5);
		Race r2 = new Race("Graduate A", now.plusMinutes(7L), dc2, Duration.ofMinutes(35), 3);
		Race r3 = new Race("Comet A", now.plusMinutes(7L), dc3, Duration.ofMinutes(35), 3);
		
		this.raceRepository.save(r1);
		this.raceRepository.save(r2);
		this.raceRepository.save(r3);

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
		
		Entry e1 = new Entry(helm1, d1, r1);
		e1.setCrew(crew1);
		Entry e2 = new Entry(helm2, d2, r1);
		e2.setCrew(crew2);
		Entry e3 = new Entry(helm3, d4, r3);
		e3.setCrew(crew3);
		
		entryRepository.save(e1);
		entryRepository.save(e2);
		entryRepository.save(e3);
		
		r1.signUp(e1);
		r1.signUp(e2);
		this.raceRepository.save(r1);
		
		r3.signUp(e3);
		this.raceRepository.save(r3);
	}
}
