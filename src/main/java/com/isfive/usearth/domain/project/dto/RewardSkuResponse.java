package com.isfive.usearth.domain.project.dto;

import java.util.Map;

import lombok.Data;

@Data
public class RewardSkuResponse {

	private Long id;
	private Integer stock;
	private Map<String, String> options;

	public RewardSkuResponse(Long id, Integer stock, Map<String, String> options) {
		this.id = id;
		this.stock = stock;
		this.options = options;
	}
}
