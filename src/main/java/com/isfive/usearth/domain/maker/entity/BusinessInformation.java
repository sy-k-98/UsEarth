package com.isfive.usearth.domain.maker.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessInformation {

	private String registrationNumber;

	private String corporateName;

	private String registration;

	@Builder
	public BusinessInformation(String registrationNumber, String corporateName, String registration) {
		this.registrationNumber = registrationNumber;
		this.corporateName = corporateName;
		this.registration = registration;
	}

}
