package com.bginfosys.dinghyracing.race;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.ManyToOne;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;

@Entity
public class Race {
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	
	@NotNull
	private String name;
	
	@NotNull
	private LocalDate date;
	
	@NotNull
	private LocalTime plannedStartTime;
	
	private @ManyToOne DinghyClass dinghyClass;
	
	//Required by JPA
	//Not recommended by Spring Data
	public Race() {}
	
	public Race(String name, LocalDate date, LocalTime plannedStartTime, DinghyClass dinghyClass) {
		this.name = name;
		this.date = date;
		this.plannedStartTime = plannedStartTime;
		this.dinghyClass = dinghyClass;
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
	
	public DinghyClass getDinghyClass() {
		return dinghyClass;
	}

	public void setDinghyClass(DinghyClass dinghyClass) {
		this.dinghyClass = dinghyClass;
	}

}
