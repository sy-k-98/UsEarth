package com.isfive.usearth.exception;

public class AuthException extends BusinessException {

	public AuthException(ErrorCode errorCode, String detail) {
		super(errorCode, detail);
	}

	public AuthException(ErrorCode errorCode) {
		super(errorCode);
	}
}
