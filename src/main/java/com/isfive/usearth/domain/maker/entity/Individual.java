package com.isfive.usearth.domain.maker.entity;

import com.isfive.usearth.domain.member.entity.Member;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Individual extends Maker {
	private String idCard;

	@Builder
	public Individual(String name, String profileImage, String email, String phone, String submitFile, String idCard,
		Member member) {
		super(name, profileImage, email, phone, submitFile, member);
		this.idCard = idCard;
	}
}
