package com.bginfosys.dinghyracing.exceptions;

import com.bginfosys.dinghyracing.model.Entry;

public class DuplicateLapException extends DomainRuleException {

	private static final long serialVersionUID = 1L;
	
	public DuplicateLapException(Entry entry) {
		super("Lap is already recorded for " + entry.toString());
	}

}
