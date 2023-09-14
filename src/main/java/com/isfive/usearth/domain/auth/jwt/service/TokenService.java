package com.isfive.usearth.domain.auth.jwt.service;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.web.common.cookie.CookieUtils;
import com.isfive.usearth.web.common.jwt.JwtTokenUtils;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
	private final JwtTokenUtils tokenUtils;
	private final MemberRepository memberRepository;
	private final StringRedisTemplate redisTemplate;
	private final int AT_COOKIE_EXPIRE_SECONDS = 600; // Access Token 쿠키 10분
	private final int RT_COOKIE_EXPIRE_SECONDS = 3600; // Refresh Token 쿠키 1시간
	private String key = "refresh";

	@PostConstruct
	public void init() {
		log.info("Token Service init");
		redisTemplate.expire(key, Duration.ofHours(1));
	}

	// TODO 토큰 생성 후 쿠키 전송
	public String setTokenAndCookie(String email, HttpServletRequest request, HttpServletResponse response) {

		CookieUtils.deleteAll(request, response);

		Optional<Member> optionalMember = memberRepository.findByEmail(email);

		if (optionalMember.isEmpty())
			return null;

		Member member = optionalMember.get();

		String accessToken = tokenUtils
			.createAccessToken(CustomUserDetails.fromEntity(member));
		String refreshToken = tokenUtils
			.createRefreshToken(CustomUserDetails.fromEntity(member));

		redisTemplate.opsForHash().put(key, email, refreshToken);

		CookieUtils.addCookie(response, "accessToken", accessToken, AT_COOKIE_EXPIRE_SECONDS);
		CookieUtils.addCookie(response, "refreshToken", refreshToken, RT_COOKIE_EXPIRE_SECONDS);

		return accessToken;
	}

	// TODO 로그아웃
	public void logout(String email, HttpServletRequest request, HttpServletResponse response) {
		CookieUtils.deleteCookie(request, response, "accessToken");
		CookieUtils.deleteCookie(request, response, "refreshToken");
		redisTemplate.opsForHash().delete(key, email);
	}
}
