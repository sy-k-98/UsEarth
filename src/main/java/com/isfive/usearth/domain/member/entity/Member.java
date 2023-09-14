package com.isfive.usearth.domain.member.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.isfive.usearth.domain.auth.jwt.service.CustomUserDetails;
import com.isfive.usearth.domain.common.BaseEntity;
import com.isfive.usearth.web.member.dto.UpdateRegister;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SQLDelete(sql = "UPDATE member SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Entity
@Getter
@Table(
	indexes = @Index(name = "idx_member_username", columnList = "username")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", nullable = false, unique = true)
	private String username;
	private String nickname;
	private String password;
	private String phone;
	private String email;
	private String provider;
	private String providerId;

	@Column(nullable = false)
	private boolean deleted = false;

	public Member updateInfo(UserDetails userDetails) {
		CustomUserDetails customUserDetails = (CustomUserDetails)userDetails;
		this.password = customUserDetails.getPassword();
		this.phone = customUserDetails.getPassword();
		this.email = customUserDetails.getEmail();
		this.provider = customUserDetails.getProvider();
		this.providerId = customUserDetails.getProviderId();
		return this;
	}

	public Member updateInfoByUpdateRegister(UpdateRegister updateRegister) {
		this.nickname = updateRegister.getNickname();
		this.phone = updateRegister.getPhone();
		this.email = updateRegister.getEmail();
		return this;
	}

	public Member updateRole(Role role) {
		this.role = role;
		return this;
	}

	public void encodePassword(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(password);
	}

	/* 연관관계 */

	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	private Member(String email, String username, String password, String nickname, String phone, String provider, Role role, String providerId) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.phone = phone;
		this.provider = provider;
		this.role = role;
		this.providerId = providerId;
	}

	/* 로직 */

	public boolean isEqualsUsername(String username) {
		return this.username.equals(username);
	}
}
