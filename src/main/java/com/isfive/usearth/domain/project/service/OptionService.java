package com.isfive.usearth.domain.project.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isfive.usearth.domain.project.entity.Option;
import com.isfive.usearth.domain.project.entity.OptionValue;
import com.isfive.usearth.domain.project.entity.Reward;
import com.isfive.usearth.domain.project.entity.RewardSku;
import com.isfive.usearth.domain.project.entity.SkuValue;
import com.isfive.usearth.domain.project.repository.OptionRepository;
import com.isfive.usearth.domain.project.repository.OptionValueRepository;
import com.isfive.usearth.domain.project.repository.RewardRepository;
import com.isfive.usearth.domain.project.repository.RewardSkuRepository;
import com.isfive.usearth.domain.project.repository.SkuValueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OptionService {

	private final RewardSkuRepository rewardSkuRepository;
	private final SkuValueRepository skuValueRepository;
	private final OptionRepository optionRepository;
	private final OptionValueRepository optionValueRepository;
	private final RewardRepository rewardRepository;

	public List<Option> convertOption(Map<String, String> options, Reward reward) {
		List<Option> optionList = new ArrayList<>();
		for (Map.Entry<String, String> pair : options.entrySet()) {
			// 옵션 이름 저장
			Option option = createOption(pair.getKey());
			optionRepository.save(option);

			// 옵션 값 리스트 저장
			List<OptionValue> optionValueList = createOptionValue(convertValueStrToList(pair.getValue()));
			for (OptionValue optionValue : optionValueList) {
				option.addOptionValue(optionValue);
				reward.addOptionValue(optionValue);
			}
			optionList.add(option);
			rewardRepository.save(reward);
			optionRepository.save(option);
		}
		optionRepository.saveAll(optionList);

		return optionList;
	}

	public Option createOption(String name) {
		Option option = Option.builder()
			.name(name)
			.build();
		optionRepository.save(option);
		return option;
	}

	public List<OptionValue> createOptionValue(List<String> optionValues) {
		List<OptionValue> optionValueList = new ArrayList<>();
		for (String value : optionValues) {
			OptionValue optionValue = OptionValue.builder()
				.value(value)
				.build();
			optionValueList.add(optionValue);
		}
		optionValueRepository.saveAll(optionValueList);
		return optionValueList;
	}

	public List<String> convertValueStrToList(String str) {
		str = str.replace("\"", "");
		return Arrays.stream(str.split(","))
			.map(String::trim)
			.filter(s -> s.length() > 0)
			.collect(Collectors.toList());
	}

	public List<RewardSku> createRewardSku(Map<String, Integer> optionStocks, Reward reward) {
		List<RewardSku> rewardSkuList = new ArrayList<>();
		for (Map.Entry<String, Integer> pair : optionStocks.entrySet()) {
			RewardSku rewardSku = RewardSku.builder()
				.initStock(pair.getValue())
				.stock(pair.getValue())
				.build();
			rewardSkuRepository.save(rewardSku);
			reward.addRewardSku(rewardSku);
			rewardSkuList.add(rewardSku);

			List<String> options = convertValueStrToList(pair.getKey());
			for (String option : options) {
				OptionValue optionValue = optionValueRepository.findByReward_IdAndValue(reward.getId(), option);
				SkuValue skuValue = SkuValue
					.builder()
					.rewardSku(rewardSku)
					.optionValue(optionValue)
					.build();
				skuValueRepository.save(skuValue);
			}
		}
		rewardSkuRepository.saveAll(rewardSkuList);
		return rewardSkuList;
	}

}
