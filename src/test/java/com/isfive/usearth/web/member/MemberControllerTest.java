package com.isfive.usearth.web.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.domain.utils.mail.MailService;
import com.isfive.usearth.web.auth.dto.SignUpRequest;
import com.isfive.usearth.web.member.dto.MailAuthenticationRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	StringRedisTemplate redisTemplate;

	@Autowired
	MemberRepository memberRepository;

	@MockBean
	MailService mailService;

	@Value("${spring.data.redis.email-key}")
	private String key;

	// private static final String BASE_URL = "http://localhost:8080";

	@Test
	public void email_인증번호_생성_테스트() throws Exception {
	    // given
		SignUpRequest signUpRequest = SignUpRequest.builder()
			.username("hyuk")
			.password("qwer")
			.passwordCheck("qwer")
			.email("fruit9370@naver.com")
			.nickname("qqqqq")
			.build();
		// when
		/**
		 * Object를 JSON으로 변환
		 * */
		String body = mapper.writeValueAsString(signUpRequest);
		//then
		mockMvc.perform(post("/members")
				.content(body) //HTTP Body에 데이터를 담는다
				.contentType(MediaType.APPLICATION_JSON) //보내는 데이터의 타입을 명시
			)
			.andExpect(status().isOk());

		HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
		ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();

		Object savedRequest = stringObjectObjectHashOperations.get(key, signUpRequest.getEmail());
		String code = stringStringValueOperations.get(signUpRequest.getEmail());

		System.out.println(savedRequest);
		System.out.println(code);
	}

	@Test
	public void 계정_생성_테스트() throws Exception {
		// given
		SignUpRequest signUpRequest = SignUpRequest.builder()
			.username("hyuk")
			.password("qwer")
			.passwordCheck("qwer")
			.email("fruit9370@naver.com")
			.nickname("qqqqq")
			.build();
		// when
		/**
		 * Object를 JSON으로 변환
		 * */
		String body = mapper.writeValueAsString(signUpRequest);
		//then
		mockMvc.perform(post("/members")
				.content(body) //HTTP Body에 데이터를 담는다
				.contentType(MediaType.APPLICATION_JSON) //보내는 데이터의 타입을 명시
			)
			.andExpect(status().isOk());

		HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
		ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();

		Object savedRequest = stringObjectObjectHashOperations.get(key, signUpRequest.getEmail());
		String code = stringStringValueOperations.get(signUpRequest.getEmail());

		System.out.println(savedRequest);
		System.out.println(code);

		MailAuthenticationRequest mailAuthenticationRequest = new MailAuthenticationRequest();
		mailAuthenticationRequest.setEmail("fruit9370@naver.com");
		mailAuthenticationRequest.setCode(code);

		body = mapper.writeValueAsString(mailAuthenticationRequest);

		mockMvc.perform(post("/members/email")
				.content(body) //HTTP Body에 데이터를 담는다
				.contentType(MediaType.APPLICATION_JSON) //보내는 데이터의 타입을 명시
			)
			.andExpect(status().isOk());

		Member member = memberRepository.findByEmailOrThrow("fruit9370@naver.com");

		Assertions.assertThat(member.getUsername()).isEqualTo(signUpRequest.getUsername());
	}


}