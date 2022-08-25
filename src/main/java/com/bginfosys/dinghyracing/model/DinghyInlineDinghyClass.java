package com.bginfosys.dinghyracing.model;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "dinghyInlineDinghyClass", types = {Dinghy.class})
public interface DinghyInlineDinghyClass {
	
	String getSailNumber();
	
	DinghyClass getDinghyClass();
}
