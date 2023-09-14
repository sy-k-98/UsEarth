package com.isfive.usearth.domain.funding.service;

import static com.isfive.usearth.exception.ErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isfive.usearth.domain.funding.dto.DeliveryRegister;
import com.isfive.usearth.domain.funding.dto.PaymentRegister;
import com.isfive.usearth.domain.funding.dto.RewardSkuRegister;
import com.isfive.usearth.domain.funding.entity.Delivery;
import com.isfive.usearth.domain.funding.entity.Funding;
import com.isfive.usearth.domain.funding.entity.FundingRewardSku;
import com.isfive.usearth.domain.funding.entity.Payment;
import com.isfive.usearth.domain.funding.repository.FundingRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.domain.project.entity.RewardSku;
import com.isfive.usearth.domain.project.repository.RewardSkuRepository;
import com.isfive.usearth.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FundingService {

	private final MemberRepository memberRepository;
	private final RewardSkuRepository rewardSkuRepository;
	private final FundingRepository fundingRepository;
	private final ProjectRepository projectRepository;

	@Transactional
	public void funding(String username,
						Long projectId, DeliveryRegister deliveryRegister,
						PaymentRegister paymentRegister,
						List<RewardSkuRegister> rewardSkuRegisters) {
		Member member = memberRepository.findByUsernameOrThrow(username);

		Delivery delivery = Delivery.createDelivery(deliveryRegister);

		Payment payment = Payment.createPayment(paymentRegister);

		Map<Long, Integer> idCountMap = createidCountMap(rewardSkuRegisters);

		List<RewardSku> rewardSkus = rewardSkuRepository.findAllByIdWithReward(idCountMap.keySet());

		List<FundingRewardSku> fundingRewardSkus = createFindingRewardSkus(rewardSkus, idCountMap);

		Funding funding = Funding.createFunding(member, delivery, payment, fundingRewardSkus);
		fundingRepository.save(funding);

		Project project = projectRepository.findByIdOrElseThrow(projectId);
		project.addTotalAmount(funding.getTotalAmount());
	}

	@Transactional
	public void cancel(String username, Long projectId, Long fundingId) {
		Funding funding = fundingRepository.findFundingToCancel(fundingId)
			.orElseThrow(() -> new EntityNotFoundException(FUNDING_NOT_FOUND));

		funding.verify(username);
		funding.cancel();

		Project project = projectRepository.findByIdOrElseThrow(projectId);
		project.removeTotalAmount(funding.getTotalAmount());
	}

	private Map<Long, Integer> createidCountMap(List<RewardSkuRegister> rewardSkuRegisters) {
		return rewardSkuRegisters.stream()
			.collect(Collectors.toMap(RewardSkuRegister::getRewardSkuId, RewardSkuRegister::getCount));
	}

	private List<FundingRewardSku> createFindingRewardSkus(List<RewardSku> rewardSkus, Map<Long, Integer> idCountMap) {
		return rewardSkus.stream()
			.map(rewardSku -> {
				Integer count = idCountMap.get(rewardSku.getId());
				Integer price = rewardSku.getPrice();
				return FundingRewardSku.createFundingRewardSku(rewardSku, count, price);
			})
			.toList();
	}
}
