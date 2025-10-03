package com.bginfosys.dinghyracing.persistence;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class SignedUpId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long raceId;
	
	private Long entryId;

	public SignedUpId() {};
	
	public SignedUpId(Long raceId, Long entryId) {
		this.raceId = raceId;
		this.entryId = entryId;				
	}
	
	public Long getRaceId() {
		return raceId;
	}

	public void setRaceId(Long raceId) {
		this.raceId = raceId;
	}

	public Long getEntryId() {
		return entryId;
	}

	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(entryId, raceId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SignedUpId other = (SignedUpId) obj;
		return Objects.equals(entryId, other.entryId) && Objects.equals(raceId, other.raceId);
	}
	
}
