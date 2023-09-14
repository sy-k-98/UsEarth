package com.isfive.usearth.domain.auth.jwt.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.exception.ConflictException;
import com.isfive.usearth.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaUserDetailsManager implements UserDetailsManager {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	// UserDetailsService.loadUserByUsername(String)
	// 실제로 Spring Security 내부에서 사용하는 반드시 구현해야 정상동작을 기대할 수 있는 메소드
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Member> optionalMember = memberRepository.findByEmail(email);
		if (optionalMember.isEmpty())
			return null;
		Member member = optionalMember.get();
		return CustomUserDetails.fromEntity(member);
	}

	@Transactional
	@Override
	// 새로운 사용자를 저장하는 메소드 (선택)
	public void createUser(UserDetails user) {
		log.info("try create user: {}", user.getUsername());
		// 사용자가 (이미) 있으면 생성할수 없다.
		if (this.userExists(user.getUsername()))
			throw new ConflictException(ErrorCode.MEMBER_CONFLICT);
		try {
			Member member = memberRepository.save(
				((CustomUserDetails)user).newEntity());
			member.encodePassword(passwordEncoder);
		} catch (ClassCastException e) {
			log.error("failed to cast to {}", CustomUserDetails.class);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	// 계정이름을 가진 사용자가 존재하는지 확인하는 메소드 (선택)
	public boolean userExists(String username) {
		log.info("check if user: {} exists", username);
		return this.memberRepository.existsByUsername(username);
	}

	@Transactional
	@Override
	public void updateUser(UserDetails user) {
		Member member = memberRepository.findByUsernameOrThrow(user.getUsername());
		member.updateInfo(user);
	}

	@Transactional
	@Override
	public void deleteUser(String username) {
		memberRepository.deleteByUsernameOrThrow(username);
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {

	}
}
