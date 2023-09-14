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
import jakarta.persistence.Table;
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
@Table(name = "options")
public class Option {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name; // 색상, 사이즈

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reward_id")
	private Reward reward;

	@OneToMany(mappedBy = "option")
	@Builder.Default
	private List<OptionValue> optionValues = new ArrayList<>();

	public void setReward(Reward reward) {
		this.reward = reward;
		if (!reward.getOptions().contains(this))
			reward.getOptions().add(this);
	}

	public void addOptionValue(OptionValue optionValue) {
		this.getOptionValues().add(optionValue);
		if (optionValue.getOption() != this)
			optionValue.setOption(this);
	}
}
