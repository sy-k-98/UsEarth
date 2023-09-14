package com.isfive.usearth.domain.project.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isfive.usearth.domain.common.Result;
import com.isfive.usearth.domain.project.dto.RewardCreate;
import com.isfive.usearth.domain.project.dto.RewardSkuResponse;
import com.isfive.usearth.domain.project.dto.RewardsResponse;
import com.isfive.usearth.domain.project.entity.Option;
import com.isfive.usearth.domain.project.entity.Reward;
import com.isfive.usearth.domain.project.entity.RewardSku;
import com.isfive.usearth.domain.project.repository.RewardRepository;
import com.isfive.usearth.domain.project.repository.RewardSkuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RewardService {

	private final RewardRepository rewardRepository;
	private final RewardSkuRepository rewardSkuRepository;
	private final OptionService optionService;

	@Transactional
	public List<Reward> createReward(List<RewardCreate> rewardCreateList) {
		List<Reward> rewards = new ArrayList<>();
		for (RewardCreate rewardCreate : rewardCreateList) {
			Reward reward = rewardCreate.toEntity();
			List<Option> options = optionService.convertOption(rewardCreate.getOptions(), reward);
			for (Option option : options)
				reward.addOption(option);

			List<RewardSku> rewardSkus = optionService.createRewardSku(rewardCreate.getOptionStocks(), reward);
			for (RewardSku rewardSku : rewardSkus)
				reward.addRewardSku(rewardSku);

			rewards.add(reward);
		}
		rewardRepository.saveAll(rewards);
		return rewards;
	}

	// TODO 조회 방식 개선
	public Result<RewardsResponse> findRewards(Long projectId) {
		List<Reward> rewards = rewardRepository.findByProject_IdWithRewardSkus(projectId);

		List<RewardsResponse> rewardsResponses = createRewardsResponse(rewards);

		Map<Long, RewardsResponse> rewardMap = createRewardsResponseMap(rewardsResponses);

		createRewardsResponseWithRewardSkus(rewards, rewardMap);
		return new Result<>(new ArrayList<>(rewardMap.values()));
	}

	private List<RewardsResponse> createRewardsResponse(List<Reward> rewards) {
		return rewards.stream()
			.map(RewardsResponse::new)
			.toList();
	}

	private Map<Long, RewardsResponse> createRewardsResponseMap(List<RewardsResponse> rewardsResponses) {
		return rewardsResponses.stream()
			.collect(Collectors.toMap(RewardsResponse::getId, reward -> reward));
	}

	private void createRewardsResponseWithRewardSkus(List<Reward> rewards, Map<Long, RewardsResponse> rewardMap) {
		rewards.forEach(reward -> addRewardSkuResponses(reward.getId(), rewardMap));
	}

	private void addRewardSkuResponses(Long rewardId, Map<Long, RewardsResponse> rewardMap) {
		List<RewardSku> rewardSkus = rewardSkuRepository.findAllByReward_Id(rewardId);
		rewardSkus.forEach(rewardSku -> {
			Map<String, String> options = new HashMap<>();
			putOptionValue(rewardSku, options);
			RewardSkuResponse rewardSkuResponse = new RewardSkuResponse(rewardSku.getId(), rewardSku.getStock(),
				options);
			RewardsResponse rewardsResponse = rewardMap.get(rewardId);
			rewardsResponse.addRewardSkuResponses(rewardSkuResponse);
		});
	}

	private void putOptionValue(RewardSku rewardSkus, Map<String, String> options) {
		rewardSkus.getSkuValues().forEach(skuValue -> {
			options.put(skuValue.getOptionName(), skuValue.getValue());
		});
	}
}
