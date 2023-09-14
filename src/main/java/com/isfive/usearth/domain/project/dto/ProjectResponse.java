package com.isfive.usearth.domain.project.dto;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.Period;
import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.entity.Reward;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ProjectResponse {

	private final Long id;
	private final String title;
	private final String summary;
	private final String story;
	private final Integer targetAmount;
	private final Integer totalFundingAmount;
	private final FileImage repImage;
	private final String makerName;
	private final String makerProfileImage;
	private final Period fundingDate;
	private final List<RewardsResponse> rewardsResponses;
	private List<FileImage> projectImages;
	private final Integer likeCount;
	private final Integer views;
	private Page<ProjectCommentResponse> projectCommentResponses;
	private boolean likedByUser;

	public ProjectResponse(Project project) {
		this.id = project.getId();
		this.title = project.getTitle();
		this.summary = project.getSummary();
		this.story = project.getStory();
		this.targetAmount = project.getTargetAmount();
		this.totalFundingAmount = project.getTotalFundingAmount();
		this.repImage = project.getRepImage();
		this.makerName = project.getMakerName();
		this.makerProfileImage = project.getMaker().getProfileImage();
		this.fundingDate = project.getFundingDate();
		this.rewardsResponses = project.getRewards().stream()
			.map(RewardsResponse::new)
			.toList();
		this.projectImages = project.getProjectFileImages();
		this.likeCount = project.getProjectLikeCount();
		this.views = project.getViews();
	}

	public void setProjectCommentResponses(Page<ProjectCommentResponse> projectCommentResponses) {
		this.projectCommentResponses = projectCommentResponses;
	}

	@Data
	private static class RewardsResponse {
		private Long id;
		private String title;
		private String description;
		private Integer price;
		private String expectedSendDate;
		private Integer deliveryFee;
		private Integer stock;
		private Integer initStock;

		private RewardsResponse(Reward reward) {
			this.id = reward.getId();
			this.title = reward.getTitle();
			this.description = reward.getDescription();
			this.price = reward.getPrice();
			this.expectedSendDate = reward.getExpectedSendDate();
			this.deliveryFee = reward.getDeliveryFee();
			this.stock = reward.getStock();
			this.initStock = reward.getInitStock();
		}
	}
}
