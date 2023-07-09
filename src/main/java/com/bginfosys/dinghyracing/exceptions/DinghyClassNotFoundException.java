package com.bginfosys.dinghyracing.exceptions;

public class DinghyClassNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 742970901383742698L;

	public DinghyClassNotFoundException(Long id) {
		super("Could not find DinghyClass with id " + id);
	}
}
