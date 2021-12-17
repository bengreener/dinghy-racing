package com.bginfosys.dinghyracing.race;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
//import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bginfosys.dinghyracing.dinghy.Dinghy;
import com.bginfosys.dinghyracing.dinghy.DinghyRepository;
//import com.bginfosys.dinghyracing.dinghy.DinghyNotFoundException;

@RepositoryRestController
public class RaceController {
	
	private final RaceRepository raceRepository;
	private final DinghyRepository dinghyRepository;
	private final UriEntityConverter uriEntityConverter;
	
	@Autowired
	public RaceController(RaceRepository raceRepository, DinghyRepository dinghyRepository, UriEntityConverter uriEntityConverter) {
		this.raceRepository = raceRepository;
		this.dinghyRepository = dinghyRepository;
		this.uriEntityConverter = uriEntityConverter;
	}

	@PutMapping("/races/{raceId}/signupdinghy")
	public void signUpDinghy(@PathVariable Long raceId, @RequestBody String dinghyUri) {
		System.out.println("/races/{raceId}/signupdinghy");
		System.out.println(String.format("raceId: %s", raceId));
		System.out.println(String.format("dinghyUri: %s", dinghyUri));
		
		Race race = raceRepository.findById(raceId)
			.orElseThrow(() -> new RaceNotFoundException(raceId));
		
		//Need to look up dinghy by dinghy class and sail number
		
		Dinghy lookedUpdinghy = dinghyRepository.findByDinghyClassAndSailNumber(dinghy.getDinghyClass(), dinghy.getSailNumber());
				//.orElseThrow(() -> new DinghyNotFoundException(dinghyId));
		
		//race.signUpDinghy(dinghy);
		//raceRepository.save(race);
		
		//return race;
	}
	
	@GetMapping("/races/test/{raceId}")
	//ResponseEntity<?> one(@PathVariable Long raceId) {
	ResponseEntity<?> one(@PathVariable Race raceId) {
		//System.out.println("/races/test/{raceId}");
		//Race race = raceRepository.findById(raceId)
		//		.orElseThrow(() -> new RaceNotFoundException(raceId));
		
		return ResponseEntity.ok(raceId);
	}
}
