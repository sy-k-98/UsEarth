package com.isfive.usearth.domain.funding.entity;

import com.isfive.usearth.domain.funding.dto.PaymentRegister;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 19)
	private String cardNumber;

	@Column(nullable = false, length = 7)
	private String cardExpiry;

	@Column(nullable = false, length = 6)
	private String birth;

	@Column(nullable = false, length = 2)
	private String pwd_2digit;

	@Builder
	private Payment(String cardNumber, String cardExpiry, String birth, String pwd_2digit) {
		this.cardNumber = cardNumber;
		this.cardExpiry = cardExpiry;
		this.birth = birth;
		this.pwd_2digit = pwd_2digit;
	}

	public static Payment createPayment(PaymentRegister paymentRegister) {
		return Payment.builder()
			.cardNumber(paymentRegister.getCardNumber())
			.cardExpiry(paymentRegister.getExpiry())
			.birth(paymentRegister.getBirth())
			.pwd_2digit(paymentRegister.getPwd_2digit())
			.build();
	}
}