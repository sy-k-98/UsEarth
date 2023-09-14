package com.isfive.usearth.domain.auth.jwt.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
	private String username;
	private String password;
	private String email;
	private String phone;
	private String provider;
	private String providerId;
	private Role role;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role.getKey()));
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public static CustomUserDetails fromEntity(Member member) {
		return CustomUserDetails.builder()
			.username(member.getUsername())
			.password(member.getPassword())
			.email(member.getEmail())
			.phone(member.getPhone())
			.provider(member.getProvider())
			.providerId(member.getProviderId())
			.role(member.getRole())
			.build();
	}

	public Member newEntity() {
		return Member.builder()
			.username(this.username)
			.password(this.password)
			.email(this.email)
			.phone(this.phone)
			.provider(this.provider)
			.providerId(this.providerId)
			.role(this.role)
			.build();
	}
}
