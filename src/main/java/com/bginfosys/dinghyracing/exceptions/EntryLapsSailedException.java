package com.bginfosys.dinghyracing.exceptions;

public class EntryLapsSailedException extends DomainRuleException {

	private static final long serialVersionUID = 1L;

	public EntryLapsSailedException() {
		super("An entry cannot sail more laps than have been set for the race.");
	}
	
	public EntryLapsSailedException(String message) {
		super(message);
	}
	
	public EntryLapsSailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
