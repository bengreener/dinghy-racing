package com.bginfosys.dinghyracing.exceptions;

import com.bginfosys.dinghyracing.model.Dinghy;

public class DinghyAlreadySignedUpException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DinghyAlreadySignedUpException() {
		super("Dinghy has already signed up for race."); 
	}
	
	public DinghyAlreadySignedUpException(Dinghy dinghy) {
		super(String.format("%s has already signed up for race.", dinghy.toString())); 
	}

}
