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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class DinghyClass {
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	
	@NotNull
	@Column(unique=true)
	private String name;

	@NotNull
	private Integer crewSize;
	
	@NotNull
	private Integer portsmouthNumber;
	
	private String externalName;
	
	//Required by JPA
	//Not recommended by Spring Data
	public DinghyClass() {}
		
	public DinghyClass(String name, Integer crewSize, Integer portsmouthNumber) {
		this.name = name;
		this.crewSize = crewSize;
		this.portsmouthNumber = portsmouthNumber;
	}
	
	public DinghyClass(String name, Integer crewSize, Integer portsmouthNumber, String externalName) {
		this.name = name;
		this.crewSize = crewSize;
		this.portsmouthNumber = portsmouthNumber;
		this.externalName = externalName;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getCrewSize() {
		return this.crewSize;
	}
	
	public void setCrewSize(Integer crewSize) {
		this.crewSize = crewSize;
	}

	public Integer getPortsmouthNumber() {
		return portsmouthNumber;
	}

	public void setPortsmouthNumber(Integer portsmouthNumber) {
		this.portsmouthNumber = portsmouthNumber;
	}

	public String getExternalName() {
		return externalName;
	}

	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}

	@Override
	public String toString() {
		return "DinghyClass [id=" + id + ", version=" + version + ", name=" + name + ", crewSize=" + crewSize
				+ ", portsmouthNumber=" + portsmouthNumber + "]";
	}
}
