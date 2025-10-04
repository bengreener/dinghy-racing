package com.bginfosys.dinghyracing.model;

import java.util.Objects;

import com.bginfosys.dinghyracing.persistence.SignedUpId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class SignedUp {

	@EmbeddedId
	SignedUpId id;
	
	@ManyToOne
	@MapsId("raceId")
	Race race;
	
	@ManyToOne
	@MapsId("entryId")
	Entry entry;
	
	private Integer position;
	
	public SignedUp() {};
	
	public SignedUp(Race race, Entry entry) {
		this.race = race;
		this.entry = entry;
	}
	
	public SignedUp(Race race, Entry entry, Integer position) {
		this.race = race;
		this.entry = entry;
		this.position = position;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "SignedUp [id=" + id + ", race=" + race + ", entry=" + entry + ", position=" + position + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(entry, id, position, race);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SignedUp other = (SignedUp) obj;
		return Objects.equals(entry, other.entry) && Objects.equals(id, other.id)
				&& Objects.equals(position, other.position) && Objects.equals(race, other.race);
	}
}
