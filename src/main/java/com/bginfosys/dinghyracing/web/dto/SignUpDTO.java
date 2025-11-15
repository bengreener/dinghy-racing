package com.bginfosys.dinghyracing.web.dto;

import java.net.URI;

public class SignUpDTO {

	private URI helm;
	private URI dinghy;
	private URI crew;
		
	public SignUpDTO() {};

	public SignUpDTO(URI helm, URI dinghy) {
		this.helm = helm;
		this.dinghy = dinghy;
	};
	
	public SignUpDTO(URI helm, URI dinghy, URI crew) {
		this.helm = helm;
		this.dinghy = dinghy;
		this.crew = crew;
	};
	
	public URI getHelm() {
		return helm;
	}
	public void setHelm(URI helm) {
		this.helm = helm;
	}
	public URI getDinghy() {
		return dinghy;
	}
	public void setDinghy(URI dinghy) {
		this.dinghy = dinghy;
	}
	public URI getCrew() {
		return crew;
	}
	public void setCrew(URI crew) {
		this.crew = crew;
	}

}
