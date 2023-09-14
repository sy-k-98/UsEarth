package com.isfive.usearth.web.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
	@Schema(example = "ID")
	@NotBlank(message = "ID를 입력하세요")
	private String username;
	@Schema(example = "nickname")
	@NotBlank(message = "닉네임을 입력하세요")
	private String nickname;
	@Schema(example = "password")
	@NotBlank(message = "비밀번호를 입력하세요")
	private String password;
	@Schema(example = "passwordCheck")
	@NotBlank(message = "비밀번호를 한번 더 입력하세요")
	private String passwordCheck;
	@Schema(example = "010-0000-0000")
	private String phone;
	@Schema(example = "user@email.com")
	@NotBlank(message = "Email 주소를 입력하세요")
	private String email;

	public boolean pwCheck() {
		if (password.equals(passwordCheck)) {
			return true;
		}
		return false;
	}
}
