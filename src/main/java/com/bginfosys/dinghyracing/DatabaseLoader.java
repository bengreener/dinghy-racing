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
		DinghyClass dc = new DinghyClass("Scorpion", 2);
		this.dinghyClassRepository.save(dc);
		
		Dinghy d1 = new Dinghy("1234", dc);
		Dinghy d2 = new Dinghy("6745", dc);
		this.dinghyRepository.save(d1);
		this.dinghyRepository.save(d2);

		LocalDateTime now = LocalDateTime.now();
		now = now.minusNanos(now.getNano()); // avoid precision issues saving and retrieving from database
		Race r = new Race("Scorpion A", now.plusMinutes(2L), dc, Duration.ofMinutes(45), 5);
		this.raceRepository.save(r);
		
		DinghyClass dc2 = new DinghyClass("Graduate", 2);
		this.dinghyClassRepository.save(dc2);
		
		Dinghy d3 = new Dinghy("2726", dc2);
		this.dinghyRepository.save(d3);
		
		Race r2 = new Race("Graduate A", now.plusMinutes(7L), dc2, Duration.ofMinutes(35), 3);
		this.raceRepository.save(r2);
		
		Competitor c1 = new Competitor("Chris Marshall");
		Competitor c2 = new Competitor("Sarah Pascal");
		competitorRepository.save(c1);
		competitorRepository.save(c2);
		
		Entry e1 = new Entry(c1, d1, r);
		Entry e2 = new Entry(c2, d2, r);
		entryRepository.save(e1);
		entryRepository.save(e2);
		
		r.signUp(e1);
		r.signUp(e2);
		this.raceRepository.save(r);
	}

}
