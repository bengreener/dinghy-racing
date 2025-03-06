package com.bginfosys.dinghyracing.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

@Entity
public class EmbeddedRace extends Race {

	@ManyToMany
	private Set<Race> hosts = new HashSet<Race>(64);

	public EmbeddedRace() {}
	
	public EmbeddedRace(String name) {
		super(name);
	}
	
	public Set<Race> getHosts() {
		return hosts;
	}

	public void setHosts(Set<Race> hosts) {
		this.hosts = hosts;
	}
}
