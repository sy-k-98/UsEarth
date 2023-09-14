package com.isfive.usearth.domain.funding.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

	@Schema(example = "서울특별시 종로구 종로3길 17 D1동 16층, 17층")
	private String detail;

	@Schema(example = "03155")
	private String zipcode;
}
