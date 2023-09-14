package com.isfive.usearth.web.auth.oauth;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.isfive.usearth.domain.auth.jwt.service.TokenService;
import com.isfive.usearth.domain.member.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
// OAuth2 통신이 성공적으로 끝났을 때, 사용하는 클래스
// JWT를 활용한 인증 구현하고 있기 때문에
// ID Provider에게 받은 정보를 바탕으로 JWT를 발급하는 역할을 하는 용도
// JWT를 발급하고 클라이언트가 저장할 수 있도록 특정 URL로 리다이렉트 시키자.
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final MemberService memberService;
	private final TokenService tokenService;
	private final RequestCache requestCache = new HttpSessionRequestCache();
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException, ServletException {
		log.info("onAuthenticationSuccess 진입");
		// OAuth2UserServiceImpl에서 반환한 DefaultOAuth2User
		// 가 저장된다.
		OAuth2User oAuth2User
			= (OAuth2User)authentication.getPrincipal();

		Map<String, Object> attributes = oAuth2User.getAttributes();

		String email = attributes.get("email").toString();

		if (!memberService.existByEmail(email)) {
			memberService.createByAttributes(attributes);
		}

		tokenService.setTokenAndCookie(email, request, response);
		clearSession(request);

		SavedRequest savedRequest = requestCache.getRequest(request, response);
		/**
		 * prevPage가 존재하는 경우 = 사용자가 직접 /login 경로로 로그인 요청
		 * 기존 Session의 prevPage attribute 제거
		 */
		String prevPage = (String) request.getSession().getAttribute("prevPage");
		if (prevPage != null) {
			request.getSession().removeAttribute("prevPage");
		}

		// 기본 URI
		String uri = "/";

		/**
		 * savedRequest 존재하는 경우 = 인증 권한이 없는 페이지 접근
		 * Security Filter가 인터셉트하여 savedRequest에 세션 저장
		 */
		if (savedRequest != null) {
			uri = savedRequest.getRedirectUrl();
		} else if (prevPage != null && !prevPage.equals("")) {
				uri = prevPage;
		}

		redirectStrategy.sendRedirect(request, response, uri);
	}

	// 로그인 실패 후 성공 시 남아있는 에러 세션 제거
	protected void clearSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}

}
