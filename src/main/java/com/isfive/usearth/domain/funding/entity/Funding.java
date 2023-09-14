package com.isfive.usearth.domain.funding.entity;

import static com.isfive.usearth.domain.funding.entity.FundingStatus.*;
import static com.isfive.usearth.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.exception.BusinessException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Funding {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Delivery delivery;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Payment payment;

	@OneToMany(mappedBy = "funding", cascade = CascadeType.ALL)
	private List<FundingRewardSku> fundingRewardSkus = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FundingStatus status;

	@Builder
	private Funding(Member member, Delivery delivery, Payment payment, FundingStatus status) {
		this.member = member;
		this.delivery = delivery;
		this.payment = payment;
		this.status = status;
	}

	public static Funding createFunding(Member member, Delivery delivery, Payment payment,
										List<FundingRewardSku> fundingRewardSkus) {
		Funding funding = Funding.builder()
				.member(member)
				.delivery(delivery)
				.payment(payment)
				.status(ORDER)
				.build();
		fundingRewardSkus.forEach(funding::addFundingRewardSku);
		return funding;
	}

	private void addFundingRewardSku(FundingRewardSku fundingRewardSku) {
		fundingRewardSkus.add(fundingRewardSku);
		fundingRewardSku.setFunding(this);
	}

	public void setStatus(FundingStatus status) {
		this.status = status;
	}

	public void verify(String username) {
		if (!member.isEqualsUsername(username)) {
			throw new BusinessException(NOT_MATCHED_FUNDING_USER);
		}
	}

	public void cancel() {
		delivery.verifyCancelable();

		if (status == CANCEL) {
			throw new BusinessException(ALREADY_CANCEL);
		}

		status = CANCEL;

		fundingRewardSkus.forEach(FundingRewardSku::cancel);

	}

	public Integer getTotalAmount() {
		return fundingRewardSkus.stream()
				.map(fundingRewardSku -> fundingRewardSku.getAmount().intValue())
				.reduce(0, Integer::sum);
	}
}
