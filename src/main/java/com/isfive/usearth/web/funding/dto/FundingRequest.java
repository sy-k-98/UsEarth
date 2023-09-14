package com.isfive.usearth.web.funding.dto;

import java.util.List;

import com.isfive.usearth.domain.funding.dto.DeliveryRegister;
import com.isfive.usearth.domain.funding.dto.PaymentRegister;
import com.isfive.usearth.domain.funding.dto.RewardSkuRegister;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FundingRequest {

	@Valid
	@NotNull(message = "배송지를 입력해야 합니다.")
	private DeliveryRequest deliveryRequest;

	@Valid
	@NotNull(message = "결제정보를 입력해야 합니다.")
	private PaymentRequest paymentRequest;

	@Valid
	@NotNull(message = "상품을 1개이상 선택해야합니다.")
	private List<RewardSkuRequest> rewardSkuRequests;

	public DeliveryRegister toDeliveryRegister() {
		return deliveryRequest.toServiceDto();
	}

	public PaymentRegister toPaymentRegister() {
		return paymentRequest.toServiceDto();
	}

	public List<RewardSkuRegister> toRewardSkuRegisters() {
		return rewardSkuRequests.stream()
			.map(RewardSkuRequest::toServiceDto)
			.toList();
	}
}
