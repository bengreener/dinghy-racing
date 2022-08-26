package com.bginfosys.dinghyracing.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bginfosys.dinghyracing.model.DinghyNotFoundException;

@RestControllerAdvice
public class DinghyNotFoundAdvice {
	
	@ResponseBody
	@ExceptionHandler(DinghyNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String dinghyNotFoundHandler(DinghyNotFoundException ex) {
		return ex.getMessage();
	}
}
