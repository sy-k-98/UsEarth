package com.isfive.usearth.domain.common;

public enum Permit {
	ACCEPT("수락"),
	REFUSE("거절");

	private final String status;

	Permit(String status) {
		this.status = status;
	}

	public String getPermit() {
		return this.status;
	}

}
