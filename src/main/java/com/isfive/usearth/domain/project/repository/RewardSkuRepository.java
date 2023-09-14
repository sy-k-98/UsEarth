package com.isfive.usearth.domain.project.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.isfive.usearth.domain.project.entity.RewardSku;

public interface RewardSkuRepository extends JpaRepository<RewardSku, Long> {

	@Query("select rs from RewardSku rs join fetch rs.reward r where rs.id in :ids")
	List<RewardSku> findAllByIdWithReward(@Param("ids") Set<Long> ids);

	@Query("select distinct rs from RewardSku rs " +
		"join fetch rs.skuValues sv " +
		"join fetch sv.optionValue ov " +
		"join fetch ov.option o " +
		"where rs.reward.id = :rewardId")
	List<RewardSku> findAllByReward_Id(@Param("rewardId") Long rewardId);
}
