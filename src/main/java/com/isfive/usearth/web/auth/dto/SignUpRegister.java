package com.isfive.usearth.web.auth.dto;

import com.isfive.usearth.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRegister {
	private String username;
	private String nickname;
	private String password;
	private String phone;
	private String email;

	public static SignUpRegister fromRequest(SignUpRequest request) {
		return SignUpRegister.builder()
			.username(request.getUsername())
			.nickname(request.getNickname())
			.password(request.getPassword())
			.phone(request.getPhone())
			.email(request.getEmail())
			.build();
	}

	public Member toEntity() {
		return Member.builder()
			.username(this.username)
			.nickname(this.nickname)
			.password(this.password)
			.phone(this.phone)
			.email(this.email)
			.build();
	}
}
