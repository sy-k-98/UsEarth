package com.isfive.usearth.domain.project.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.isfive.usearth.domain.common.BaseEntity;
import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.Period;
import com.isfive.usearth.domain.maker.entity.Maker;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.exception.BusinessException;
import com.isfive.usearth.exception.ErrorCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE project SET deleted = true WHERE id = ? and version = ?")
@Where(clause = "deleted = false")
public class Project extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String summary;

	private String story;

	private Integer targetAmount;

	private Integer totalFundingAmount;

	@Embedded
	private FileImage repImage; // 대표이미지

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "maker_id")
	private Maker maker;

	@Embedded
	private Period fundingDate;

	private boolean deleted = false;

	private Integer views;

	private Integer likeCount;

	private Integer commentCount;

	@Version
	private Long version;

	@OneToMany(mappedBy = "project")
	@Builder.Default
	private List<Tag> searchTags = new ArrayList<>();

	@OneToMany(mappedBy = "project")
	@Builder.Default
	private List<Reward> rewards = new ArrayList<>();

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ProjectFileImage> projectImages = new ArrayList<>();

	@OneToMany(mappedBy = "project")
	private List<ProjectComment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "project")
	private List<ProjectLike> likes = new ArrayList<>();

	public void setMaker(Maker maker) {
		this.maker = maker;
		maker.getProjects().add(this);
	}

	public void setRepImage(FileImage fileImage) {
		this.repImage = fileImage;
	}

	public void addTag(Tag tag) {
		this.getSearchTags().add(tag);
		if (tag.getProject() != this)
			tag.setProject(this);
	}

	public void addReward(Reward reward) {
		this.getRewards().add(reward);
		if (reward.getProject() != this)
			reward.setProject(this);
	}

	public void addProjectFileImage(ProjectFileImage image) {
		this.getProjectImages().add(image);
		if (image.getProject() != this)
			image.setProject(this);
	}

	public void update(Project projectUpdate) {
		if (projectUpdate.getTitle() != null)
			this.title = projectUpdate.getTitle();
		if (projectUpdate.getSummary() != null)
			this.summary = projectUpdate.getSummary();
		if (projectUpdate.getRepImage() != null)
			this.repImage = projectUpdate.getRepImage();
	}

	public String getMakerName() {
		return maker.getName();
	}

	public List<FileImage> getProjectFileImages() {
		return projectImages.stream()
			.map(ProjectFileImage::getFileImage)
			.toList();
	}

	public Integer getProjectLikeCount() {
		return likes == null ? 0 : likes.size();
	}

	public void verifyWriter(String username) {
		if (!member.isEqualsUsername(username)) {
			throw new BusinessException(ErrorCode.PROJECT_WRITER_ALLOW);
		}
	}

	public void verifyNotWriter(String username) {
		if (member.isEqualsUsername(username)) {
			throw new BusinessException(ErrorCode.PROJECT_WRITER_NOT_ALLOW);
		}
	}

	public void verifyNotDeleted() {
		if (deleted) {
			throw new BusinessException(ErrorCode.ALREADY_DELETED_PROJECT);
		}
	}

	public void increaseView() {
		views++;
	}

	public void increaseLikeCount() {
		likeCount++;
	}

	public void increaseCommentCount() {
		commentCount++;
	}

	public void decreaseCommentCount() {
		commentCount--;
	}

	public void cancelLike() {
		if (likeCount > 0) {
			likeCount--;
		}
	}

	public void addTotalAmount(Integer totalAmount) {
		totalFundingAmount += totalAmount;
	}

	public void removeTotalAmount(Integer totalAmount) {
		if (totalFundingAmount < totalAmount) {
			throw new RuntimeException("취소 금액이 현재 펀딩 금액 보다 큽니다.");
		}
		totalFundingAmount -= totalAmount;
	}

	public boolean isAchieve() {
		return totalFundingAmount > targetAmount;
	}
}
