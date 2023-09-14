package com.isfive.usearth.web.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MailAuthenticationRequest {
	@Schema(example = "user@email.com")
	@NotBlank(message = "Email 주소를 입력해주세요.")
	private String email;
	@Schema(example = "123456")
	@NotBlank(message = "인증 번호를 입력해주세요.")
	private String code;
}
