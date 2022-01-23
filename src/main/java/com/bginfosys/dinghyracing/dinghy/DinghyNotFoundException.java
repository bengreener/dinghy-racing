package com.bginfosys.dinghyracing.dinghy;

public class DinghyNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public DinghyNotFoundException(Long id) {
		super("Could not find dinghy with id " + id);
	}

}
