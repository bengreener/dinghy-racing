package com.bginfosys.dinghyracing.web.dto;

import java.time.Duration;

public class LapDTO {
	private Integer number;
	
	private Duration time;

	public LapDTO(Integer number, Duration time) {
		this.number = number;
		this.time = time;
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
