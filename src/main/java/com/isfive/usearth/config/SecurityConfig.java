package com.isfive.usearth.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.isfive.usearth.domain.auth.oauth.service.OAuth2UserServiceImpl;
import com.isfive.usearth.web.auth.oauth.OAuth2SuccessHandler;
import com.isfive.usearth.web.common.jwt.JwtTokenFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtTokenFilter jwtTokenFilter;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2UserServiceImpl oAuth2UserServiceImpl;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
				.requestMatchers(
						new AntPathRequestMatcher("/favicon.ico"),
						new AntPathRequestMatcher("/error/**")
				)
				.requestMatchers(toH2Console());
	}

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(authHttp -> authHttp
				.requestMatchers(
					new AntPathRequestMatcher("/login/**"),
					new AntPathRequestMatcher("/projects", "GET"),
					new AntPathRequestMatcher("/projects/{projectId}", "GET"),
					new AntPathRequestMatcher("/members", "POST"),
					new AntPathRequestMatcher("/members/email", "POST"),
					new AntPathRequestMatcher("/makers/{makerId}", "GET"),
					new AntPathRequestMatcher("/members/login", "POST"),
					new AntPathRequestMatcher("/boards/{boardId}/posts", "GET"),
					new AntPathRequestMatcher("/posts/{postId}", "GET"),
					new AntPathRequestMatcher("/swagger-ui/**", "GET"),
					new AntPathRequestMatcher("/v3/api-docs/**", "GET"),
					new AntPathRequestMatcher("/ws-stomp/**")
				).permitAll()
				.anyRequest()
				.authenticated()
			)
			.oauth2Login(oauth2Login -> oauth2Login
				.loginPage("/login")
				.successHandler(oAuth2SuccessHandler)
				.userInfoEndpoint(userInfo -> userInfo
					.userService(oAuth2UserServiceImpl)
				)
			)
			.sessionManagement(
				sessionManagement -> sessionManagement
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtTokenFilter, AuthorizationFilter.class);
		return http.build();
	}

}
