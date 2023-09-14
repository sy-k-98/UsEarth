package com.isfive.usearth.web.maker.dto;

import lombok.Getter;

@Getter
public class MakerUpdate {
	private String phone;
	private String email;

	private MakerUpdate(MakerUpdateRequest makerUpdateRequest) {
		this.phone = makerUpdateRequest.getPhone();
		this.email = makerUpdateRequest.getEmail();
	}

	public static MakerUpdate toMakerUpdate(MakerUpdateRequest makerUpdateRequest) {
		return new MakerUpdate(makerUpdateRequest);
	}
}
