package com.bginfosys.dinghyracing.model;

import java.time.Duration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Lap implements Comparable<Lap> {
	
	@Id	
	@GeneratedValue 
	private Long id;
	
	private @Version @JsonIgnore Long version;
	
	@NotNull
	private Integer number;
	
	@NotNull
	private Duration time;

	public Lap() {};
	
	public Lap(Integer number, Duration time) {
		this.number = number;
		this.time = time;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Duration getTime() {
		return time;
	}

	public void setTime(Duration time) {
		this.time = time;
	}

	@Override
	public int compareTo(Lap o) {
		if (this.getNumber() > o.getNumber()) {
			return 1;
		}
		if (this.getNumber() < o.getNumber()) {
			return -1;
		}
		return 0;
	}
}
