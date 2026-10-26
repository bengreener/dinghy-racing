package com.bginfosys.dinghyracing.service;

public class ExportException extends ServiceException {

	private static final long serialVersionUID = 1L;
	
	public ExportException(String message) {
		super(message);
	}
	
	public ExportException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
