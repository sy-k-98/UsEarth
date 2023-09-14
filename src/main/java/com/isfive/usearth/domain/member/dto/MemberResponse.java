package com.isfive.usearth.domain.member.dto;

import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
	private Long id;
	private String username;
	private String nickname;
	private String phone;
	private String email;
	private Role role;

	public static MemberResponse fromEntity(Member member) {
		return MemberResponse.builder()
			.id(member.getId())
			.username(member.getUsername())
			.nickname(member.getNickname())
			.phone(member.getPhone())
			.email(member.getEmail())
			.role(member.getRole())
			.build();
	}
}
