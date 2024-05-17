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

import java.time.Duration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.bginfosys.dinghyracing.validation.constraints.DurationPositive;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Records a lap time for an {@link com.bginfosys.dinghyracing.model.Entry Entry}
 */
@Entity
public class Lap implements Comparable<Lap> {
	
	@Id	
	@GeneratedValue 
	private Long id;
	
	private @Version @JsonIgnore Long version;
	
	@NotNull
	private Integer number;
	
//	@NotNull
	@DurationPositive
	private Duration time;

	public Lap() {};
	
	public Lap(Integer number, Duration time) {
		this.number = number;
		this.time = time;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Duration getTime() {
		return time;
	}

	/**
	 * Set time for the lap. 
	 * @param time a positive value greater than zero
	 */
	public void setTime(Duration time) {
		this.time = time;
	}

	@Override
	public int compareTo(Lap o) {
		if (this.getNumber() > o.getNumber()) {
			return 1;
		}
		if (this.getNumber() < o.getNumber()) {
			return -1;
		}
		return 0;
	}
}
