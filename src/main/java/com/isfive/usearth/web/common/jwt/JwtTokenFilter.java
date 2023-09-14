package com.isfive.usearth.web.common.jwt;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.isfive.usearth.domain.auth.jwt.service.CustomUserDetails;
import com.isfive.usearth.domain.auth.jwt.service.TokenService;
import com.isfive.usearth.web.common.cookie.CookieUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
	private final JwtTokenUtils jwtTokenUtils;
	private final TokenService tokenService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		if(request.getCookies() != null) {
			log.info("쿠키의 수 {}", request.getCookies().length);
		}

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (!(authHeader != null && authHeader.startsWith("Bearer ")) || request.getRequestURI().equals("/favicon.ico")) {
			log.info("no Cookie uri = {}", request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = authHeader.split(" ")[1];

		log.info("Access Token 검증");
		String email = jwtTokenUtils.validateAccessToken(accessToken);
		/*
			Access Token validate 실패시
			쿠키 삭제 및 에러 전송
		*/
		if (email == null) {
			log.info("Access Token validate 실패");
			CookieUtils.deleteCookie(request, response, "accessToken");
			response.sendError(401);
			filterChain.doFilter(request, response);
			return;
		}
		// Token 재발급 필요시
		else if (!email.equals("ok")) {
			log.info("Token 재발급 필요");
			Optional<Cookie> optionalCookie = CookieUtils.getCookie(request, "refreshToken");
			// Refresh Token 존재 확인
			if (optionalCookie.isPresent()) {
				String refreshToken = optionalCookie.toString();
				email = jwtTokenUtils.validateRefreshToken(refreshToken);
				// Refresh Token validate 실패시
				if (email == null) {
					log.info("Refresh Token validate 실패");
					CookieUtils.deleteCookie(request, response, "refreshToken");
					response.sendError(401);
					filterChain.doFilter(request, response);
					return;
				}// Access Token 재발급 가능
				else {
					log.info("Access Token 재발급 가능");
					accessToken = tokenService.setTokenAndCookie(email, request, response);
				}
			}
			else  {
				log.info("Refresh Token validate 실패");
				CookieUtils.deleteCookie(request, response, "refreshToken");
				response.sendError(401);
				filterChain.doFilter(request, response);
				return;
			}
		} else {
			log.info("Token 재발급 필요 없음");
		}

		SecurityContext context
			= SecurityContextHolder.createEmptyContext();

		// JWT에서 사용자 정보 가져오기
		UserDetails userDetails = jwtTokenUtils
			.getUserDetails(accessToken);

		if (userDetails == null) {
			log.info("userDetails == null 다음 filter");
			filterChain.doFilter(request, response);
			return;
		}
		// 사용자 인증 정보 생성
		AbstractAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(
			CustomUserDetails.builder()
				.username(userDetails.getUsername())
				.build(),
			accessToken, userDetails.getAuthorities()
		);

		log.info("userDetails.getUsername() = {}", userDetails.getUsername());

		// SecurityContext에 사용자 정보 설정
		context.setAuthentication(authenticationToken);

		// SecurityContextHolder에 SecurityContext 설정
		SecurityContextHolder.setContext(context);

		log.info("set security context with jwt");

		filterChain.doFilter(request, response);
	}
}
