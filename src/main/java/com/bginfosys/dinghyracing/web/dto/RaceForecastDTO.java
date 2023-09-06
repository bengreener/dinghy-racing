package com.bginfosys.dinghyracing.web.dto;

import java.time.Duration;

public class RaceForecastDTO {
	private Duration leadBoatLastLapTime;
	
	private Duration leadBoatAverageLapTime;
	
	private Double lapEstimate;

	public RaceForecastDTO() {}
	
	public RaceForecastDTO(Duration leadBoatLastLapTime, Duration leadBoatAverageLapTime, Double lapEstimate) {
		this.leadBoatLastLapTime = leadBoatLastLapTime;
		this.leadBoatAverageLapTime = leadBoatAverageLapTime;
		this.lapEstimate = lapEstimate;
	}

	public Duration getLeadBoatLastLapTime() {
		return leadBoatLastLapTime;
	}

	public void setLeadBoatLastLapTime(Duration leadBoatLastLapTime) {
		this.leadBoatLastLapTime = leadBoatLastLapTime;
	}

	public Duration getLeadBoatAverageLapTime() {
		return leadBoatAverageLapTime;
	}

	public void setLeadBoatAverageLapTime(Duration leadBoatAverageLapTime) {
		this.leadBoatAverageLapTime = leadBoatAverageLapTime;
	}

	public Double getLapEstimate() {
		return lapEstimate;
	}

	public void setLapEstimate(Double lapEstimate) {
		this.lapEstimate = lapEstimate;
	}
}
