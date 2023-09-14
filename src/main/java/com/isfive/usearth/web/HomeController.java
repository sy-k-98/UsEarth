package com.isfive.usearth.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/login")
public class HomeController {

	@GetMapping
	public String login(
			HttpServletRequest request
	) {

		/**
		 * 이전 페이지로 되돌아가기 위한 Referer 헤더값을 세션의 prevPage attribute로 저장
		 */
		String uri = request.getHeader("Referer");
		if (uri != null && !uri.contains("/login")) {
			request.getSession().setAttribute("prevPage", uri);
		}
		return "index";
	}
}
