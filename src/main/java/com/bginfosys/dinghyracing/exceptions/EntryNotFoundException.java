package com.bginfosys.dinghyracing.exceptions;

public class EntryNotFoundException extends DomainRuleException {

	private static final long serialVersionUID = 1L;
	
	public EntryNotFoundException(Long id) {
		super("Could not find entry with id " + id);
	}
	
}
