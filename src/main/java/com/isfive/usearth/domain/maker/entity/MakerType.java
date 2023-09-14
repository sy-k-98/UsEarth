package com.isfive.usearth.domain.maker.entity;

public enum MakerType {
	INDIVIDUAL("개인"),
	PERSONAL_BUSINESS("개인사업자"),
	CORPORATE_BUSINESS("법인사업자");

	private final String category;

	MakerType(String category) {
		this.category = category;
	}

	public String getMakerType() {
		return this.category;
	}

}
