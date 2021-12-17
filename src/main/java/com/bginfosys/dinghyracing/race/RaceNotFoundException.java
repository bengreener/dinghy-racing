package com.bginfosys.dinghyracing.race;

public class RaceNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RaceNotFoundException(Long id) {
		super("Could not find race with id " + id);
	}
}
