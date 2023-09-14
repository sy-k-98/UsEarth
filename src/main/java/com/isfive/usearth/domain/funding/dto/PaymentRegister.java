package com.isfive.usearth.domain.funding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRegister {
	private String cardNumber;
	private String expiry;
	private String birth;
	private String pwd_2digit;
}
