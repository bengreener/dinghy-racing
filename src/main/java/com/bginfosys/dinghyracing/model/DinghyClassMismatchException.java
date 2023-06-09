package com.bginfosys.dinghyracing.model;

public class DinghyClassMismatchException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DinghyClassMismatchException() {
		super("Dinghy class does not match required class.");
	}
	
}
