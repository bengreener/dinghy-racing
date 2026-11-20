package com.bginfosys.dinghyracing.exceptions;

import com.bginfosys.dinghyracing.model.Entry;

public class EntryHasFinishedRaceException extends DomainRuleException {

	private static final long serialVersionUID = 1L;
	
	public EntryHasFinishedRaceException(Entry entry) {
		super(entry.toString() + " has finished the race");
	}
	
}
