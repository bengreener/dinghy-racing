package com.bginfosys.dinghyracing.race;

import java.time.LocalDate;
import java.time.LocalTime;

public class Race {
	private LocalDate date;
	private LocalTime plannedStartTime;
	
	public Race() {	
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setPlannedStartTime(LocalTime time) {
		plannedStartTime = time;
	}
	
	public LocalTime getPlannedStartTime() {
		return plannedStartTime;
	}
}
