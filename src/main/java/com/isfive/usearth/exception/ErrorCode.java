package com.isfive.usearth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M_001", "사용자를 찾을 수 없습니다."),
	BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "B_001", "게시판을 찾을 수 없습니다."),
	POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P_001", "게시글을 찾을 수 없습니다."),
	PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PR_001", "프로젝트를 찾을 수 없습니다."),
	PROJECT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRC_001", "프로젝트 댓글을 찾을 수 없습니다."),
	FUNDING_NOT_FOUND(HttpStatus.NOT_FOUND, "F_001", "펀딩을 찾을 수 없습니다."),

	POST_WRITER_NOT_ALLOW(HttpStatus.BAD_REQUEST, "PW_001", "작성자는 해당 기능을 사용할 수 없습니다."),
	POST_WRITER_ALLOW(HttpStatus.BAD_REQUEST, "PW_001", "다른 사용자는 해당 기능을 사용할 수 없습니다."),
	PROJECT_WRITER_NOT_ALLOW(HttpStatus.BAD_REQUEST, "PRW_001", "작성자는 해당 기능을 사용할 수 없습니다."),
	PROJECT_WRITER_ALLOW(HttpStatus.BAD_REQUEST, "PRW_001", "다른 사용자는 해당 기능을 사용할 수 없습니다."),
	NOT_MATCHED_FUNDING_USER(HttpStatus.BAD_REQUEST, "NMFU_001", "해당 펀딩의 후원자가 아닙니다."),
	ALREADY_CANCEL(HttpStatus.BAD_REQUEST, "AC_001", "이미 취소된 펀딩입니다."),
	ALREADY_DELETED_POST(HttpStatus.BAD_REQUEST, "AD_001", "이미 삭제된 게시글입니다."),
	ALREADY_DELETED_PROJECT(HttpStatus.BAD_REQUEST, "ADPR_001", "이미 삭제된 프로젝트입니다."),
	ALREADY_DELETED_PROJECT_COMMENT(HttpStatus.BAD_REQUEST, "ADPRC_001", "이미 삭제된 프로젝트 댓글입니다."),
	MEMBER_CONFLICT(HttpStatus.CONFLICT, "M_002", "이미 등록된 사용자입니다."),
	ALREADY_START_DELIVERY(HttpStatus.CONFLICT, "ASD_001", "배송이 시작되어 상품을 취소할 수 없습니다."),
	ALREADY_DELIVERY_COMPLETED(HttpStatus.CONFLICT, "ADC_001", "배송이 완료된 상품은 취소할 수 없습니다."),

	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "PA_001", "비밀번호가 일치하지 않습니다."),

	BAD_REQUEST_COOKIE(HttpStatus.BAD_REQUEST, "CK_001", "쿠키가 존재하지 않습니다."),

	SEND_EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E_001", "이메일을 전송할 수 없습니다."),
	TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TK_001", "유효하지 않은 토큰입니다."),
	AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AF_", "권한이 없습니다.");

	ErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
