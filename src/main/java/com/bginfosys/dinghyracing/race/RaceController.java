/*package com.bginfosys.dinghyracing.race;

import java.util.List;
import java.util.Optional;

import java.lang.Long;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bginfosys.dinghyracing.dinghy.Dinghy;
import com.bginfosys.dinghyracing.dinghy.DinghyRepository;

@RepositoryRestController
public class RaceController {
	
	private final RaceRepository raceRepository;
	private final DinghyRepository dinghyRepository;
	
	@Autowired
	public RaceController(RaceRepository raceRepository, DinghyRepository dinghyRepository) {
		this.raceRepository = raceRepository;
		this.dinghyRepository = dinghyRepository;
	}

	@PatchMapping("/races/{raceId}/signupdinghy")
	public void signUpDinghy(@PathVariable Long raceId, @RequestBody Links dinghies) {
		Long dinghyId;
		Race race;
		Dinghy dinghy;
		String[] uriComponents;
		
		//System.out.println("/races/{raceId}/signupdinghy");
		//System.out.println(String.format("raceId: %s", raceId));
		
		race = raceRepository.findById(raceId).get();
		//System.out.println(String.format("Race: %s", race.toString()));		
		
		//Need to look up dinghies by dinghy class and sail number
		//Get Id from dinghy URI
		for(Link link : dinghies) {
			//System.out.println(String.format("Path: %s", link.toUri().getPath()));
			uriComponents = link.toUri().getPath().split("/");
			dinghyId = Long.parseLong(uriComponents[uriComponents.length - 1]);
			//System.out.println(String.format("Dinghy Id: %s", dinghyId));
			dinghy = dinghyRepository.findById(dinghyId).get();
			System.out.println(String.format("Dinghy: %s", dinghy.toString()));
			race.signUpDinghy(dinghy);
		};
		
		raceRepository.save(race);
	}
}
*/
