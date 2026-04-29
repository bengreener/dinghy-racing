package com.bginfosys.dinghyracing.model;

import java.time.Duration;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

@Entity
public class SignedUp {

	protected @Id @GeneratedValue Long id;
	protected @Version @JsonIgnore Long version;
	
	@ManyToOne
	Race race;
	
	@ManyToOne
	Entry entry;
	
	private Duration correctedTime;
	
	private Integer position;
	
	public SignedUp() {};
	
	public SignedUp(Race race, Entry entry) {
		this.race = race;
		this.entry = entry;
	}

	public Long getId() {
		return id;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public Entry getEntry() {
		return entry;
	}

	public void setEntry(Entry entry) {
		this.entry = entry;
	}

	public Duration getCorrectedTime() {
		// if null return a duration of infinity to avoid null issues when performing operations on duration
		if (correctedTime == null) {
			return Duration.ofSeconds((long)Double.POSITIVE_INFINITY);
		}
		return correctedTime;
	}

	public void setCorrectedTime(Duration correctedTime) {
		// if value is equal to infinity then a lap has not been completed and no corrected time can be calculated
		if (correctedTime.getSeconds() == (long)Double.POSITIVE_INFINITY) {
			this.correctedTime = null;
		}
		else {
			this.correctedTime = correctedTime;
		}
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "SignedUp [id=" + id + ", version=" + version + ", race=" + race + ", entry=" + entry + ", correctedTime=" + correctedTime
				+ ", position=" + position + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(entry, id, correctedTime, position, race, version);
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
				&& Objects.equals(correctedTime,  other.correctedTime)
				&& Objects.equals(position, other.position) && Objects.equals(race, other.race)
				&& Objects.equals(version, other.version);
	}	
}
