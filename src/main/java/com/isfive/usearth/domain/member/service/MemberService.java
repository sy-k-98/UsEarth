package com.isfive.usearth.domain.member.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isfive.usearth.domain.member.dto.MemberResponse;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.entity.Role;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.domain.utils.mail.MailService;
import com.isfive.usearth.exception.BusinessException;
import com.isfive.usearth.exception.ErrorCode;
import com.isfive.usearth.web.auth.dto.SignUpRegister;
import com.isfive.usearth.web.member.dto.UpdateRegister;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;

	@Transactional
	public MemberResponse createBy(SignUpRegister signUpRegister) {
		Member member = memberRepository.save(signUpRegister.toEntity().updateRole(Role.USER));
		member.encodePassword(passwordEncoder);
		return MemberResponse.fromEntity(member);
	}

	@Transactional
	public MemberResponse createByAttributes(Map<String, Object> attributes) {
		Member member = Member.builder()
			.username("sns-" + UUID.randomUUID())
			.password(UUID.randomUUID().toString())
			.email(attributes.get("email").toString())
			.provider(attributes.get("provider").toString())
			.providerId(attributes.get("id").toString())
			.role(Role.USER)
			.build();
		member.encodePassword(passwordEncoder);
		Member save = memberRepository.save(member);
		return MemberResponse.fromEntity(save);
	}

	@Transactional
	public MemberResponse updateUserInfoByUpdateRegister(String username, UpdateRegister updateRegister) {
		Member member = memberRepository.findByUsernameOrThrow(username);
		member.updateInfoByUpdateRegister(updateRegister);
		return MemberResponse.fromEntity(member);
	}

	@Transactional
	public void deleteBy(String username) {
		memberRepository.deleteByUsernameOrThrow(username);
	}

	public MemberResponse readByUsername(String username) {
		Member member = memberRepository.findByUsernameOrThrow(username);
		return MemberResponse.fromEntity(member);
	}

	public boolean checkUser(String username, String password) {
		Member member = memberRepository.findByUsernameOrThrow(username);
		return passwordEncoder.matches(password, member.getPassword());
	}

	public boolean existByEmail(String email) {
		return memberRepository.existsByEmail(email);
	}

	public void sendCodeToEmail(String toEmail, String code) throws Exception {
		this.checkDuplicatedEmail(toEmail);
		mailService.sendEmail(toEmail, code);
	}

	private void checkDuplicatedEmail(String email) {
		if (memberRepository.existsByEmail(email)) {
			log.debug("MemberService.checkDuplicatedEmail exception occur email: {}", email);
			throw new BusinessException(ErrorCode.MEMBER_CONFLICT);
		}
	}
}
