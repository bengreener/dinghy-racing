package com.bginfosys.dinghyracing;

import java.time.LocalDate;
import java.time.LocalTime;

public class Race {
	private LocalDate date;
	private LocalTime time;
	
	public Race() {	
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setTime(LocalTime time) {
		this.time = time;
	}
	
	public LocalTime getTime() {
		return time;
	}
}
