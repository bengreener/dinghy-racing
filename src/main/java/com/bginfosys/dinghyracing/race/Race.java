package com.bginfosys.dinghyracing.race;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Race {
	
	private @Id @GeneratedValue Long id;
	
	private String name;
	private LocalDate date;
	private LocalTime plannedStartTime;
	
	private @Version @JsonIgnore Long version;
	
	public Race() {	
	}
	
	public Race(String name, LocalDate date, LocalTime plannedStartTime) {
		this.name = name;
		this.date = date;
		this.plannedStartTime = plannedStartTime;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
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
