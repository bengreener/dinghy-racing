package com.bginfosys.dinghyracing.model;

//public class DinghyNotFoundException extends RuntimeException {
public class DinghyNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public DinghyNotFoundException(Long id) {
		super("Could not find dinghy with id " + id);
	}

}
