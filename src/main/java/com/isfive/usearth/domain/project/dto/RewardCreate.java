package com.isfive.usearth.domain.project.dto;

import java.util.Map;

import com.isfive.usearth.domain.project.entity.Reward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardCreate {
	private String title;
	private String description;
	private Integer price;
	private String expectedSendDate;
	private Integer deliveryFee;
	private Map<String, String> options;
	private Map<String, Integer> optionStocks;

	public Reward toEntity() {
		return Reward.builder()
			.title(title)
			.description(description)
			.price(price)
			.expectedSendDate(expectedSendDate)
			.deliveryFee(deliveryFee)
			.build();
	}

}
