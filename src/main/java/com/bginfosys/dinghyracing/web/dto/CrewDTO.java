package com.bginfosys.dinghyracing.web.dto;

import com.bginfosys.dinghyracing.model.Competitor;

public class CrewDTO {

	private Competitor helm;
	
	private Competitor crew;

	public CrewDTO(Competitor helm, Competitor crew) {
		this.helm = helm;
		this.crew = crew;
	}

	public Competitor getHelm() {
		return helm;
	}

	public void setHelm(Competitor helm) {
		this.helm = helm;
	}

	public Competitor getCrew() {
		return crew;
	}

	public void setCrew(Competitor crew) {
		this.crew = crew;
	}
}
