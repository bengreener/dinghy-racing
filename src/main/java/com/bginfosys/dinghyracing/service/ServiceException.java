package com.bginfosys.dinghyracing.service;

public abstract class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	// Prevent stack trace from being exposed to client
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
	
}
