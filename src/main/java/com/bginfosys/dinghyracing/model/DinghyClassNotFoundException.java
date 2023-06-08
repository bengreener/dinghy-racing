package com.bginfosys.dinghyracing.model;

//public class DinghyClassNotFoundException extends RuntimeException {
public class DinghyClassNotFoundException extends Exception {
	
	private static final long serialVersionUID = 742970901383742698L;

	public DinghyClassNotFoundException(Long id) {
		super("Could not find DinghyClass with id " + id);
	}
}
