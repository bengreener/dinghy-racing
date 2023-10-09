package com.bginfosys.dinghyracing.exceptions;

//public class RaceNotFoundException extends RuntimeException {
public class RaceNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RaceNotFoundException(Long id) {
		super("Could not find race with id " + id);
	}
}
