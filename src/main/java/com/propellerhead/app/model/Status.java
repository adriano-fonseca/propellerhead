package com.propellerhead.app.model;

public enum Status {
	PROSPECTIVE("P", "Prospective"), CURRENT("C", "Current"), NONACTIVE("NA", "Non-active");

	private final String key;

	private final String value;

	Status(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
