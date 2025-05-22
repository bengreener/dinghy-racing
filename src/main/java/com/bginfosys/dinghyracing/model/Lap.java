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
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

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

	@Override
	public String toString() {
		return "Lap [id=" + id + ", version=" + version + ", number=" + number + ", time=" + time + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, number, time, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lap other = (Lap) obj;
		return Objects.equals(id, other.id) && Objects.equals(number, other.number) && Objects.equals(time, other.time)
				&& Objects.equals(version, other.version);
	}
}
