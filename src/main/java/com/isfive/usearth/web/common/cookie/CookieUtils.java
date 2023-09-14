package com.isfive.usearth.web.common.cookie;

import java.util.Base64;
import java.util.Optional;

import org.springframework.util.SerializationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {

	private CookieUtils() {
		throw new IllegalStateException("인스턴스화 할 수 없습니다.");
	}

	public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name))
					return Optional.of(cookie);
			}
		}
		return Optional.empty();
	}

	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static void updateCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					cookie.setValue(value);
					cookie.setMaxAge(maxAge);
					response.addCookie(cookie);
				}
			}
		}
	}

	public static void deleteCookie(
		HttpServletRequest request,
		HttpServletResponse response,
		String name
	) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					cookie.setPath("/");
					cookie.setValue("");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}

	public static void deleteAll(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				Cookie newCookie = new Cookie(cookie.getName(), null);
				newCookie.setPath("/");
				newCookie.setMaxAge(0);
				response.addCookie(newCookie);
			}
		}
	}

	public static String serialize(Object object) {
		return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
	}

	public static <T> T deserialize(Cookie cookie, Class<T> tClass) {
		return tClass.cast(SerializationUtils.deserialize(
			Base64.getUrlDecoder().decode(cookie.getValue())
		));
	}
}
