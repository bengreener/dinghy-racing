package com.bginfosys.dinghyracing.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bginfosys.dinghyracing.service.DropboxException;

@RestControllerAdvice
public class DropboxExceptionAdvice {
	
	@ResponseBody
	@ExceptionHandler(DropboxException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	DropboxException dropboxExceptionHandler(DropboxException ex) {
		return ex;
	}

}
