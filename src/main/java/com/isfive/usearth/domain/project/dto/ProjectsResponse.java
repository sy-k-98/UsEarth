package com.isfive.usearth.domain.project.dto;

import java.time.LocalDateTime;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.Period;
import com.isfive.usearth.domain.project.entity.Project;

import lombok.Data;

@Data
public class ProjectsResponse {

	private final Long id;
	private final String title;
	private final FileImage fileImage;
	private final Period fundingDate;
	private final Integer targetAmount;
	private final Integer totalFundingAmount;
	private final Integer views;
	private final Integer likeCount;
	private final Integer commentCount;
	private final LocalDateTime createdDate;
	private Boolean likedByUser;

	public ProjectsResponse(Project project) {
		this.id = project.getId();
		this.title = project.getTitle();
		this.fileImage = project.getRepImage();
		this.fundingDate = project.getFundingDate();
		this.targetAmount = project.getTargetAmount();
		this.totalFundingAmount = project.getTotalFundingAmount();
		this.views = project.getViews();
		this.likeCount = project.getLikeCount();
		this.commentCount = project.getCommentCount();
		this.createdDate = project.getCreatedDate();
		this.likedByUser = false;
	}
}
