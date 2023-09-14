package com.isfive.usearth.domain.utils.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.isfive.usearth.exception.BusinessException;
import com.isfive.usearth.exception.ErrorCode;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String username;

	public static int createNumber() {
		return (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
	}

	public void sendEmail(
		String toEmail,
		String code
	) throws Exception {
		// SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
		MimeMessage message = createMessage(toEmail, code);
		try {
			javaMailSender.send(message);
		} catch (RuntimeException e) {
			log.error("MailService.sendEmail exception occur toEmail: {}, " +
				"code: {}", toEmail, code);
			throw new BusinessException(ErrorCode.SEND_EMAIL_FAILED);
		}
	}

	private MimeMessage createMessage(String email, String code) throws Exception {
		MimeMessage message = javaMailSender.createMimeMessage();

		message.addRecipients(Message.RecipientType.TO, email);
		message.setSubject("UsEarth 이메일 인증 코드입니다.");
		message.setText("이메일 인증코드: " + code);

		message.setFrom(new InternetAddress(username + "@naver.com", "UsEarth"));

		return message;
	}

	public void sendErrorEmail(String toEmail, String code) throws Exception {
		MimeMessage message = createErrorMessage(toEmail, code);
		try {
			javaMailSender.send(message);
		} catch (RuntimeException e) {
			log.error("MailService.sendEmail exception occur toEmail: {}, " +
				"code: {}", toEmail, code);
			throw new BusinessException(ErrorCode.SEND_EMAIL_FAILED);
		}
	}

	private MimeMessage createErrorMessage(String email, String code) throws Exception {
		MimeMessage message = javaMailSender.createMimeMessage();

		message.addRecipients(Message.RecipientType.TO, email);
		message.setSubject("UsEarth 결제 관련 오류 메일입니다.");
		message.setText(code);
		message.setFrom(new InternetAddress(username + "@naver.com", "UsEarth"));

		return message;
	}

}
