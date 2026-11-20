package com.bginfosys.dinghyracing.exceptions;

import com.bginfosys.dinghyracing.model.Entry;

public class EntryHasScoringAbbreviationException extends DomainRuleException {

	private static final long serialVersionUID = 1L;
	
	public EntryHasScoringAbbreviationException(Entry entry) {
		super(entry.toString() + " has a scoring abbreviation");
	}
	
}
