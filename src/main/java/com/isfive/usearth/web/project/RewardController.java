package com.isfive.usearth.web.project;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.isfive.usearth.domain.common.Result;
import com.isfive.usearth.domain.project.dto.RewardsResponse;
import com.isfive.usearth.domain.project.service.RewardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "3. Project", description = "Project API")
public class RewardController {

	private final RewardService rewardService;

	@Operation(summary = "프로젝트 리워드 조회")
	@GetMapping("/projects/{projectId}/rewards")
	public ResponseEntity<Result<RewardsResponse>> findRewards(@PathVariable("projectId") Long projectId) {
		return new ResponseEntity<>(rewardService.findRewards(projectId), HttpStatus.OK);
	}
}
