package com.bginfosys.dinghyracing.web.controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bginfosys.dinghyracing.model.Competitor;
import com.bginfosys.dinghyracing.web.dto.CrewDTO;

@RestController
@RequestMapping("/dinghyracing/api/crews")
public class CrewController {

	@GetMapping(path= "/search/findCrewsByDinghy")
	public ResponseEntity<Set<CrewDTO>> findCrewsByDinghy(@RequestParam(name = "dinghy") String dinghyUri) {
		ResponseEntity<Set<CrewDTO>> responseEntity;
		
		Set<CrewDTO> crews = new HashSet<CrewDTO>();
				
//		CrewDTO crew1 = new CrewDTO(new Competitor(), new Competitor());
//		CrewDTO crew2 = new CrewDTO(new Competitor(), new Competitor());
//		crews.add(new CrewDTO(new Competitor(), new Competitor()));
		
		responseEntity = ResponseEntity.ok()
			.header("Content-Type", "application/hal+json")
			.body(crews);
		
		return responseEntity;
	}
}
