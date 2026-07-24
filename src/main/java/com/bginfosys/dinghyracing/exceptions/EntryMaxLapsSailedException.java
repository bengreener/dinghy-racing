package com.bginfosys.dinghyracing.exceptions;

public class EntryMaxLapsSailedException extends DomainRuleException {

	private static final long serialVersionUID = 1L;

	public EntryMaxLapsSailedException() {
		super("An entry cannot sail more laps than have been set for the race.");
	}
	
	public EntryMaxLapsSailedException(String message) {
		super(message);
	}
	
	public EntryMaxLapsSailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
