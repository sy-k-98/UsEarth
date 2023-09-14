package com.isfive.usearth.web.maker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MakerUpdateRequest {
	@Schema(example = "010-1111-1111")
	@NotBlank(message = "전화번호를 입력해주세요.")
	private String phone;

	@Schema(example = "newMaker@email.com")
	@NotBlank(message = "이메일을 입력해주세요.")
	private String email;
}
