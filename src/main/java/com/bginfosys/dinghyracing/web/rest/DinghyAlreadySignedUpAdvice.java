package com.bginfosys.dinghyracing.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bginfosys.dinghyracing.exceptions.DinghyAlreadySignedUpException;

@RestControllerAdvice
public class DinghyAlreadySignedUpAdvice {

	@ResponseBody
	@ExceptionHandler(DinghyAlreadySignedUpException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	String dinghyAlreadySignedUpHandler(DinghyAlreadySignedUpException ex) {
		return ex.getLocalizedMessage();
	}
}
