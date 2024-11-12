package com.bginfosys.dinghyracing.web.controllers;

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
	public ResponseEntity<Object> findCrewsByDinghy(@RequestParam(name = "dinghy") String dinghyUri) {
		ResponseEntity<Object> responseEntity;
		CrewDTO crew = new CrewDTO(new Competitor(), new Competitor());
		
		responseEntity = ResponseEntity.ok()
			.header("Content-Type", "application/hal+json")
			.body(crew);
		
		return responseEntity;
	}
}
