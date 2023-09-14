package com.isfive.usearth.domain.project.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RewardSku {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer stock;

	private Integer initStock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reward_id")
	private Reward reward;

	@OneToMany(mappedBy = "rewardSku")
	@Builder.Default
	private List<SkuValue> skuValues = new ArrayList<>();

	public void setReward(Reward reward) {
		this.reward = reward;
		if (!reward.getRewardSkus().contains(this))
			reward.getRewardSkus().add(this);
	}

	public Integer getPrice() {
		return reward.getPrice();
	}

	public void removeStock(Integer count) {
		if (stock < count) {
			// TODO 예외 변경
			throw new RuntimeException("재고가 부족합니다.");
		}
		stock -= count;
	}

	public void addStock(Integer count) {
		stock += count;
	}
}
