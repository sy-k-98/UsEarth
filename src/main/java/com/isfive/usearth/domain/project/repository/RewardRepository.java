package com.isfive.usearth.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.isfive.usearth.domain.project.entity.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long> {

	@Query("select distinct r from Reward r join fetch r.rewardSkus rs where r.project.id = :projectId")
	List<Reward> findByProject_IdWithRewardSkus(@Param("projectId") Long projectId);
}
