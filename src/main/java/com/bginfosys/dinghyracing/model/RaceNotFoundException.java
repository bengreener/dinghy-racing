package com.bginfosys.dinghyracing.model;

public class RaceNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RaceNotFoundException(Long id) {
		super("Could not find race with id " + id);
	}
}
