package com.bginfosys.dinghyracing.dinghy;

import org.springframework.data.rest.core.config.Projection;

import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;

@Projection(name = "inlineDinghyClass", types = {Dinghy.class})
interface InlineDinghyClass {
	
	String getSailNumber();
	
	DinghyClass getDinghyClass();
}
