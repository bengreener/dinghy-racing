package com.bginfosys.dinghyracing.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bginfosys.dinghyracing.model.RaceNotFoundException;

@RestControllerAdvice
public class RaceNotFoundAdvice {
	
	@ResponseBody
	@ExceptionHandler(RaceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String raceNotFoundHandler(RaceNotFoundException ex) {
		return ex.getMessage();
	}
}
