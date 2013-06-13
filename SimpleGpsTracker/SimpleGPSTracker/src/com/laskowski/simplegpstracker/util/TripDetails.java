package com.laskowski.simplegpstracker.util;

public class TripDetails {

	private String description;
	private long id;

	public TripDetails(String desc, long id) {
		this.description = desc;
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return description;
	}

}
