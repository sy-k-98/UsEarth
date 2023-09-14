package com.isfive.usearth.exception;

public class CookieProcessingException extends BusinessException {

	public CookieProcessingException(ErrorCode errorCode, String detail) {
		super(errorCode, detail);
	}

	public CookieProcessingException(ErrorCode errorCode) {
		super(errorCode);
	}
}
