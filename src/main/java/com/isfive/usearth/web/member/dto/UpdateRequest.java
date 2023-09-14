package com.isfive.usearth.web.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateRequest {
	@Schema(example = "nickname")
	private String nickname;
	@Schema(example = "010-0000-0000")
	private String phone;
	@Schema(example = "user@email.com")
	private String email;

	public UpdateRegister toRegister() {
		return UpdateRegister.builder()
			.nickname(this.nickname)
			.phone(this.phone)
			.email(this.email)
			.build();
	}
}
