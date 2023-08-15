package com.bginfosys.dinghyracing.model;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.util.Set;
import java.util.HashSet;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Race {
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	
	@NotNull
	@NaturalId
	private String name;
	
	@NotNull
	@NaturalId
	private LocalDateTime plannedStartTime;
	
	private LocalDateTime actualStartTime;
	
	@ManyToOne
	private DinghyClass dinghyClass;
	
	@NotNull
	private Duration duration;
	
	@Column(unique=true)
	@OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Entry> signedUp;
	
	//Required by JPA
	//Not recommended by Spring Data
	public Race() {}
	
	public Race(String name, LocalDateTime plannedStartTime, DinghyClass dinghyClass, Duration duration) {
		this.name = name;
		this.plannedStartTime = plannedStartTime;
		this.dinghyClass = dinghyClass;
		this.duration = duration;
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
	
	public LocalDateTime getPlannedStartTime() {
		return plannedStartTime;
	}

	public void setPlannedStartTime(LocalDateTime plannedStartTime) {
		this.plannedStartTime = plannedStartTime;
	}
	
	public LocalDateTime getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(LocalDateTime actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public DinghyClass getDinghyClass() {
		return dinghyClass;
	}

	public void setDinghyClass(DinghyClass dinghyClass) {
		this.dinghyClass = dinghyClass;
	}
	
	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Set<Entry> getSignedUp() {
		return signedUp;
	}
	
	public void setSignedUp(Set<Entry> signedUp) {
		this.signedUp = signedUp;
	}
	
	public void signUp(Entry entry) {
		if (signedUp == null) {
			signedUp = new HashSet<Entry>(64);
		}
		signedUp.add(entry);
	}
	
	public String toString() {
		return (name + ", " + plannedStartTime.toString() + ", " + dinghyClass.getName());
	}
}
