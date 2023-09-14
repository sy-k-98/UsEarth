package com.isfive.usearth.exception;

public class ConflictException extends BusinessException {

	public ConflictException(ErrorCode errorCode, String detail) {
		super(errorCode, detail);
	}

	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}
}
