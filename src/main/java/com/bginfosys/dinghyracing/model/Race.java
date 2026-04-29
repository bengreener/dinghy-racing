/*
 * Copyright 2022-2024 BG Information Systems Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
   
package com.bginfosys.dinghyracing.model;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Version;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Transient;

import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Race implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Transient
	Logger logger = LoggerFactory.getLogger(Race.class);
	
	protected @Id @GeneratedValue Long id;
	protected @Version @JsonIgnore Long version;
	
	@NotNull
	protected String name;
	
	@NotNull
	@ManyToOne
	protected Fleet fleet;
	
	@Column(unique=true)
	@OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("entry_id ASC")
	protected Set<SignedUp> signedUp = new HashSet<SignedUp>(64);
	
	// tried making this @Transient but value not retained. Appears to be unsafe to assume Spring will use the same instance of the entity
	// tried setting JsonIgnore but link was still output in Json
	@ManyToOne
	protected Entry lastLeadEntry; // used to check if positions need to be recalculated because leadEntry has changed; for example if a lap is removed from the last lead entry
	
	// track laps completed by lead entry in database so race id (and ETag) are updated when the lead entry completes a lap without the lead entry changing
	// a better solution to remove lead entry information from race entity and provide through a seperate API call to a controller? (log technical debt and attempt this as quick solution for now)
	@JsonIgnore
	protected Integer lastLeadEntryLapsCompleted;
	
	//Required by JPA
	//Not recommended by Spring Data
	public Race() {}
	
	public Race(String name, Fleet fleet) {
		this.name = name;
		this.fleet = fleet;
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

	public Fleet getFleet() {
		return fleet;
	}
	
	public void setFleet(Fleet fleet) {
		this.fleet = fleet;
	}

	public Set<SignedUp> getSignedUp() {
		if (signedUp == null) {
			signedUp = new HashSet<SignedUp>(64);
		}
		return signedUp;
	}
	
	public void setSignedUp(Set<SignedUp> signedUp) {
		this.signedUp = signedUp;
	}

	public Entry getLastLeadEntry() {
		return lastLeadEntry;
	}
	
	public Integer leadEntrylapsCompleted() {
		return signedUp != null ? signedUp.stream().mapToInt(signedUp -> signedUp.getEntry().getLaps().size()).max().orElse(0) : 0;
	}
	
	public Entry getLeadEntry() {
		if (signedUp != null && signedUp.size() > 0 ) {
			// get entries that have completed the same number of laps as lead boat
			Integer leadLapCount = this.leadEntrylapsCompleted();
			Stream<SignedUp> entriesOnLeadLap = signedUp.stream().filter(signedUp -> signedUp.getEntry().getLaps().size() == leadLapCount);
			// return entry on lead lap with lowest sum of lap times
			Optional<Entry> optional = entriesOnLeadLap.min((SignedUp signedUp1, SignedUp signedUp2) -> signedUp1.getEntry().getSumOfLapTimes().compareTo(signedUp2.getEntry().getSumOfLapTimes())).map(SignedUp::getEntry);
			return optional.orElse(null);
		}
		return null;
	}
	
	public boolean onLastLap(Entry entry) {
		return false;
	}
	
	public boolean completedLastLap(Entry entry) {
		return false;
	}
	
	public abstract void updatePositions(SignedUp signedUp);
	
	@Override
	public String toString() {
		return "Race [id=" + id + ", version=" + version + ", name=" + name + ", fleet=" + fleet + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(fleet, id, name, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Race other = (Race) obj;
		return Objects.equals(fleet, other.fleet)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(version, other.version);
	}
}

