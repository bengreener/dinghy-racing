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

package com.bginfosys.dinghyracing.web.model.proxy;

import org.springframework.hateoas.EntityModel;

import com.bginfosys.dinghyracing.model.Competitor;

/**
 * Crew is a derived concept in the model; it does not map to a persisted entity.
 * Crew is based on the unique combination of helm and mate that have sailed a dinghy.
 * Each dinghy can have multiple crew combinations and each crew combination can have sailed multiple dinghies.
 * Created in web.model.proxy package as will return adata structure modelled around web API not underlying model API.
 */
public class Crew {
	
	EntityModel<Competitor> helm;
	
	EntityModel<Competitor> mate;
	
	public Crew(EntityModel<Competitor> helm, EntityModel<Competitor> mate) {
		this.helm = helm;
		this.mate = mate;
	}

	public EntityModel<Competitor> getHelm() {
		return helm;
	}

	public void setHelm(EntityModel<Competitor> helm) {
		this.helm = helm;
	}

	public EntityModel<Competitor> getMate() {
		return mate;
	}

	public void setCrew(EntityModel<Competitor> mate) {
		this.mate = mate;
	}
}
