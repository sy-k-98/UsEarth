package com.isfive.usearth.web.common.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.isfive.usearth.domain.auth.jwt.service.CustomUserDetails;
import com.isfive.usearth.domain.auth.jwt.service.JpaUserDetailsManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenUtils {
	private final Key signingKey;
	private final Key refreshKey;
	private final StringRedisTemplate redisTemplate;
	private final JwtParser jwtParser;
	private final JpaUserDetailsManager userDetailsManager;
	public final Long ACCESS_TOKEN_EXPIRATION_TIME;
	public final Long REFRESH_TOKEN_EXPIRATION_TIME;

	public JwtTokenUtils(
		@Value("${security.jwt.base-64-secret}")
		String jwtSecret,
		@Value("${security.jwt.refresh-secret}")
		String refreshSecret,
		@Value("${security.jwt.access-expiration-time}") Long accessExpirationTime,
		@Value("${security.jwt.refresh-expiration-time}") Long refreshExpirationTime,
		JpaUserDetailsManager userDetailsManager,
		StringRedisTemplate redisTemplate
	) {
		this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
		// jwt 번역기 만들기
		this.jwtParser = Jwts.parserBuilder()
			.setSigningKey(this.signingKey)
			.build();
		this.ACCESS_TOKEN_EXPIRATION_TIME = accessExpirationTime;
		this.REFRESH_TOKEN_EXPIRATION_TIME = refreshExpirationTime;
		this.userDetailsManager = userDetailsManager;
		this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
		this.redisTemplate = redisTemplate;
	}

	public String validateAccessToken(String token) {
		try {
			// 검증
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);

			// access 토큰의 만료시간이 지났을경우 재발급을 위한 email 반환
			if (!claims.getBody().getExpiration().after(new Date())) {
				return claims.getBody().getSubject();
			}
			return "ok";
		} catch (Exception e) {
			log.warn("invalid jwt: {}", e.getClass());
			return null;
		}
	}

	public String validateRefreshToken(String token) {
		try {
			// 검증
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
			String email = claims.getBody().getSubject();

			// Redis에 저장한 refresh token 과 동일한가?
			if (!redisTemplate.opsForHash().get("refresh", email).equals(token))
				return null;

			// refresh 토큰의 만료시간이 지나지 않았을 경우 재발급을 위한 email 반환
			if (claims.getBody().getExpiration().after(new Date())) {
				return email;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
		return null;
	}

	// JWT를 인자로 받고, 그 JWT를 해석해서
	// 사용자 정보를 회수하는 메소드
	public Claims parseClaims(String token) {
		return jwtParser
			.parseClaimsJws(token)
			.getBody();
	}

	public UserDetails getUserDetails(String token) {
		log.info("this.parseClaims(token).getSubject() = {}", this.parseClaims(token).getSubject());
		return userDetailsManager.loadUserByUsername(this.parseClaims(token).getSubject());
	}

	public String createAccessToken(CustomUserDetails userDetails) {
		return generateToken(userDetails, ACCESS_TOKEN_EXPIRATION_TIME, signingKey);
	}

	public String createRefreshToken(CustomUserDetails userDetails) {
		return generateToken(userDetails, REFRESH_TOKEN_EXPIRATION_TIME, refreshKey);
	}

	// 주어진 사용자 정보를 바탕으로 JWT를 문자열로 생성
	private String generateToken(CustomUserDetails userDetails, long expirationTime, Key key) {
		// Claims: JWT에 담기는 정보의 단위를 Claim이라 부른다.
		//         Claims는 Claim들을 담기위한 Map의 상속 interface
		Claims jwtClaims = Jwts.claims()
				// 사용자 정보 등록
				.setSubject(userDetails.getEmail())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime));

		return Jwts.builder()
				.setClaims(jwtClaims)
				.signWith(key)
				.compact();
	}
}
