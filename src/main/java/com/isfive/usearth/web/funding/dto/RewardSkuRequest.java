package com.isfive.usearth.web.funding.dto;

import com.isfive.usearth.domain.funding.dto.RewardSkuRegister;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RewardSkuRequest {

	@Schema(example = "1")
	private Long rewardSkuId;
	@Schema(example = "2")
	private Integer count;

	public RewardSkuRegister toServiceDto() {
		return new RewardSkuRegister(rewardSkuId, count);
	}
}

