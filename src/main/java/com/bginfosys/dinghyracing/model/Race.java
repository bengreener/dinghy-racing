package com.bginfosys.dinghyracing.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.util.Set;
import java.util.HashSet;

import javax.validation.constraints.NotNull;

import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"name", "dinghy_class_id"}))
public class Race {
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	
	@NotNull
	private String name;
	
	@NotNull
	private LocalDateTime plannedStartTime;
	
	@ManyToOne
	private DinghyClass dinghyClass;
	
	@OneToMany
	private Set<Entry> signedUp;
	
	//Required by JPA
	//Not recommended by Spring Data
	public Race() {}
	
	//public Race(String name, LocalDate date, LocalTime plannedStartTime, DinghyClass dinghyClass) {
	public Race(String name, LocalDateTime plannedStartTime, DinghyClass dinghyClass) {
		this.name = name;
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

	public void setPlannedStartTime(LocalDateTime plannedStartTime) {
		this.plannedStartTime = plannedStartTime;
	}
	
	public LocalDateTime getPlannedStartTime() {
		return plannedStartTime;
	}
	
	public DinghyClass getDinghyClass() {
		return dinghyClass;
	}

	public void setDinghyClass(DinghyClass dinghyClass) {
		this.dinghyClass = dinghyClass;
	}
	
	public Set<Entry> getSignedUp() {
		return signedUp;
	}
	
	public void setSignedUp(Set<Entry> signedUp) {
		this.signedUp = signedUp;
	}
	
	public void signUp(Entry entry) {
		if (this.signedUp == null) {
			this.signedUp = new HashSet<Entry>();
		}
		if (this.getDinghyClass() == null || (entry.getDinghy().getDinghyClass() == this.getDinghyClass())) {
			signedUp.add(entry);
		}
		else {
			throw new DinghyClassMismatchException();
		}
	}
	
	public String toString() {
		return (name + ", " + plannedStartTime.toString() + ", " + dinghyClass.getName());
	}
}
