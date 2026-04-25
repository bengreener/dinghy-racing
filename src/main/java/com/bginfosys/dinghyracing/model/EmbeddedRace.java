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

import java.util.Set;

import com.bginfosys.dinghyracing.exceptions.CompetitorAlreadySignedUpException;
import com.bginfosys.dinghyracing.exceptions.DinghyAlreadySignedUpException;
import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;
import com.bginfosys.dinghyracing.exceptions.EntryNotInHostRaceException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class EmbeddedRace extends Race {

	private static final long serialVersionUID = 1L;
		
	@Column(unique=true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
	protected Set<Race> hosts;
	
	public EmbeddedRace() {
		super();
	};
	
	public Set<Race> getHosts() {
		return hosts;
	}

	public void setHosts(Set<Race> hosts) {
		this.hosts = hosts;
	}
	
	public RaceType getType() {
		// get type of hosts (all hosts must be of the same type)
		DirectRace hostRace = (DirectRace) hosts.toArray()[0];
		return hostRace.getType();
	}

	// Signup entry for race
	public SignedUp signUp(Entry entry) {
		// entry must be signedUp to a host race
		SignedUp hostSignedUp = entry.getSignedUpTo().stream().filter(e -> e.getRace() instanceof DirectRace).findFirst().get();
		DirectRace host = (DirectRace) hostSignedUp.getRace();
		if (!this.hosts.contains(host)) {
			throw new EntryNotInHostRaceException();
		}
		// signing_up_dinghy_class_allowed_by_race
		if (!(this.fleet.getDinghyClasses().size() == 0 || this.fleet.getDinghyClasses().contains(entry.getDinghy().getDinghyClass())  )) {
			throw new DinghyClassMismatchException();
		}
		// signingup_helm_not_entered_in_race
		if (this.signedUp.stream().anyMatch(signedUp -> signedUp.getEntry().getHelm() == entry.getHelm())) {
			throw new CompetitorAlreadySignedUpException(entry.getHelm());
		}
		// signingup_dinghy_not_entered_in_race
		if (this.signedUp.stream().anyMatch(signedUp -> signedUp.getEntry().getDinghy() == entry.getDinghy())) {
			throw new DinghyAlreadySignedUpException(entry.getDinghy());
		}
		// signingup_mate_not_entered_in_race
		if (this.signedUp.stream().anyMatch(signedUp -> signedUp.getEntry().getCrew() == entry.getCrew())) {
			throw new CompetitorAlreadySignedUpException(entry.getCrew());
		}
		SignedUp signedUp = new SignedUp(this, entry);
		entry.addSignedUp(signedUp);
		this.signedUp.add(signedUp);
		return signedUp;
	}

	@Override
	public void updatePositions(SignedUp signedUp) {
		// TODO Auto-generated method stub
		
	}
}
