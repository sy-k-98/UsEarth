package com.isfive.usearth.web.project.dto;

import com.isfive.usearth.domain.project.dto.RewardCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardRegister {

	@Schema(example = "리워드 제목")
	@NotBlank(message = "리워드 제목을 입력해야 합니다.")
	private String title;

	@Schema(example = "리워드 설명")
	@NotBlank(message = "리워드 설명을 입력해야 합니다.")
	private String description;

	@Schema(example = "10000")
	@NotNull(message = "리워드 가격을 입력해야 합니다.")
	private Integer price;

	@Schema(example = "2024-01-01")
	@NotBlank(message = "배송 예정 날짜를 입력해야 합니다.")
	private String expectedSendDate;

	@Schema(example = "3000")
	@NotNull(message = "배송비를 입력해야 합니다.")
	private Integer deliveryFee;

	@Schema(example = "{\"색상\": \"검정,빨강,흰색\",\"사이즈\": \"S,M,L\"}")
	@NotEmpty(message = "옵션명과 옵션 상세내용 입력해야 합니다. 예시) \"색상\": \"검정,흰색\", \"사이즈\": \"S,M,L\"")
	private Map<String, String> options;

	@Schema(example = "{\"검정,S\": 100,\"검정,M\": 100,\"검정,L\": 100}")
	@NotEmpty(message = "옵션상세내용과 초기 수량을 입력해야 합니다. 예시) \"검정,S\": 100, \"검정,M\": 100, \"검정,L\": 100 등")
	private Map<String, Integer> optionStocks;

	public RewardCreate toService() {
		return RewardCreate.builder()
			.title(title)
			.description(description)
			.price(price)
			.expectedSendDate(expectedSendDate)
			.deliveryFee(deliveryFee)
			.options(options)
			.optionStocks(optionStocks)
			.build();
	}
}
