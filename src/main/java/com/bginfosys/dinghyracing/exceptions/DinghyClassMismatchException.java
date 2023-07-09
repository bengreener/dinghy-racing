package com.bginfosys.dinghyracing.exceptions;

public class DinghyClassMismatchException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DinghyClassMismatchException() {
		super("Dinghy class does not match required class.");
	}
	
}
