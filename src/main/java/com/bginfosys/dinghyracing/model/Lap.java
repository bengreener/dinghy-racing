package com.bginfosys.dinghyracing.model;

import java.time.Duration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.bginfosys.dinghyracing.exceptions.LapZeroOrLessTimeException;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Records a lap time for an {@link com.bginfosys.dinghyracing.model.Entry Entry}
 */
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
		this.setNumber(number);
		this.setTime(time);
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

	/**
	 * Set time for the lap. 
	 * @param time a positive value greater than zero
	 */
	public void setTime(Duration time) {
		if (time.isNegative() || time.isZero()) {
			throw new LapZeroOrLessTimeException("A lap should take longer than zero seconds");
		}
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
