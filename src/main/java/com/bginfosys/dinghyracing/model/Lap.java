package com.bginfosys.dinghyracing.model;

import java.time.Duration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Lap {
	
	@Id	
	@GeneratedValue 
	private Long id;
	
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
}
