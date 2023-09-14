package com.isfive.usearth.domain.project.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
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
public class OptionValue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "option_value")
	private String value;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reward_id")
	private Reward reward;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "options_id")
	private Option option;

	@OneToMany(mappedBy = "optionValue")
	@Builder.Default
	private List<SkuValue> skuValues = new ArrayList<>();

	public void setOption(Option option) {
		this.option = option;
		if (!option.getOptionValues().contains(this))
			option.getOptionValues().add(this);
	}

	public void setReward(Reward reward) {
		this.reward = reward;
		if (!reward.getOptionValues().contains(this))
			reward.getOptionValues().add(this);
	}

	public String getOptionName() {
		return option.getName();
	}
}
