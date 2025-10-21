package com.bginfosys.dinghyracing.exceptions;

import com.bginfosys.dinghyracing.model.Competitor;

public class CompetitorAlreadySignedUpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CompetitorAlreadySignedUpException() {
		super("Competitor has already signed up for race."); 
	}
	
	public CompetitorAlreadySignedUpException(Competitor competitor) {
		super(String.format("%s has already signed up for race.", competitor.toString())); 
	}
}
