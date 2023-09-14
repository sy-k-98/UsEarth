package com.isfive.usearth.web.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.isfive.usearth.exception.AuthException;
import com.isfive.usearth.exception.BusinessException;
import com.isfive.usearth.exception.ConflictException;
import com.isfive.usearth.exception.CookieProcessingException;
import com.isfive.usearth.exception.EntityNotFoundException;
import com.isfive.usearth.exception.FileProcessingException;
import com.isfive.usearth.exception.InvalidValueException;
import com.isfive.usearth.web.common.dto.Message;

import lombok.Data;

@RestControllerAdvice
public class ControllerAdvice {
	/**
	 * Entity 가 존재하지 않음
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Message> entityNotFound(final EntityNotFoundException exception) {

		return new ResponseEntity<>(new Message(exception.getLocalizedMessage()), exception.getStatus());
	}

	/**
	 * FileControl 에러
	 */
	@ExceptionHandler(FileProcessingException.class)
	public ResponseEntity<Message> fileProcessingException(final FileProcessingException exception) {

		return new ResponseEntity<>(new Message(exception.getLocalizedMessage()), exception.getStatus());
	}

	/**
	 * 인증 에러
	 */
	@ExceptionHandler(AuthException.class)
	public ResponseEntity<Message> authException(final AuthException exception) {

		return new ResponseEntity<>(new Message(exception.getLocalizedMessage()), exception.getStatus());
	}

	/**
	 * 중복 에러
	 */
	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<Message> conflictException(final ConflictException exception) {

		return new ResponseEntity<>(new Message(exception.getLocalizedMessage()), exception.getStatus());
	}

	/**
	 * InvalidValue 에러
	 */
	@ExceptionHandler(InvalidValueException.class)
	public ResponseEntity<Message> invalidValueException(final InvalidValueException exception) {

		return new ResponseEntity<>(new Message(exception.getLocalizedMessage()), exception.getStatus());
	}

	/**
	 * Cookie 에러
	 */
	@ExceptionHandler(CookieProcessingException.class)
	public ResponseEntity<Message> cookieProcessingException(final CookieProcessingException exception) {

		return new ResponseEntity<>(new Message(exception.getLocalizedMessage()), exception.getStatus());
	}

	/**
	 * Business 에러
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Message> businessException(final BusinessException exception) {

		return new ResponseEntity<>(new Message(exception.getLocalizedMessage()), exception.getStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidExceptionMessage> methodArgumentNotValidException(
		final MethodArgumentNotValidException exception) {
		BindingResult bindingResult = exception.getBindingResult();
		ValidExceptionMessage validExceptionMessage = new ValidExceptionMessage();

		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			validExceptionMessage.addFieldMessage(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return new ResponseEntity<>(validExceptionMessage, HttpStatus.BAD_REQUEST);
	}

	@Data
	private static class ValidExceptionMessage {

		private int statusCode = 400;

		private Map<String, String> fieldMessages = new HashMap<>();

		public void addFieldMessage(String field, String message) {
			fieldMessages.put(field, message);
		}
	}
}
