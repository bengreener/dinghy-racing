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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.ManyToOne;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Dinghy {
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	
	@NaturalId
	@NotNull
	private String sailNumber;
	
	@NaturalId
	@NotNull
	@ManyToOne
	private DinghyClass dinghyClass;
	
	public Dinghy() {}
	
	public Dinghy(String sailNumber, DinghyClass dinghyClass) {
		this.sailNumber = sailNumber;
		this.dinghyClass = dinghyClass;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSailNumber() {
		return this.sailNumber;
	}
	
	public void setSailNumber(String sailNumber) {
		this.sailNumber = sailNumber;
	}
	
	public DinghyClass getDinghyClass() {
		return this.dinghyClass;
	}
	
	public void setDinghyClass(DinghyClass dinghyClass) {
		this.dinghyClass = dinghyClass;
	}

	public String toString() {
		return sailNumber + ", " + dinghyClass.getName();
	}
}
