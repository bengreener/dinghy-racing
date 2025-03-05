package com.bginfosys.dinghyracing.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class EmbeddedRaceTests {

	@Test
	void when_hostsIsSet_then_returnsHosts() {
		EmbeddedRace er = new EmbeddedRace();
		Race r = new Race();
		Set<Race> hosts = new HashSet<Race>();
		hosts.add(r);
		er.setHosts(hosts);
		
		assertEquals(hosts, er.getHosts());		
	}
}
