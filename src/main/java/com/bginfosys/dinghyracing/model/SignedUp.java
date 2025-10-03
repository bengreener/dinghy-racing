package com.bginfosys.dinghyracing.model;

import java.util.Objects;

import com.bginfosys.dinghyracing.persistence.SignedUpId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class SignedUp {

	@EmbeddedId
	SignedUpId id;
	
	private Integer position;
	
	public SignedUp() {};
	
	public SignedUp(Integer position) {
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
		return "SignedUp [id=" + id + ", position=" + position + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, position);
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
		return Objects.equals(id, other.id) && Objects.equals(position, other.position);
	}
}
