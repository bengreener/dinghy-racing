package com.bginfosys.dinghyracing.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bginfosys.dinghyracing.model.DinghyClassNotFoundException;

@RestControllerAdvice
public class DinghyClassNotFoundAdvice {
	
	@ResponseBody
	@ExceptionHandler(DinghyClassNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String dinghyClassNotFoundHandler(DinghyClassNotFoundException ex) {
		return ex.getMessage();
	}
}
