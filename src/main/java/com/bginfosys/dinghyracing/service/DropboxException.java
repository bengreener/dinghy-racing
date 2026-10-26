package com.bginfosys.dinghyracing.service;

public class DropboxException extends ServiceException {

	private static final long serialVersionUID = 1L;
	
	public DropboxException(String message) {
		super(message);
	}
	
	public DropboxException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
