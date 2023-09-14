package com.isfive.usearth.domain.project.dto;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.Period;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.project.entity.Project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreate {
	private String title;
	private String summary;
	private String story;
	private Integer targetAmount;
	private Period fundingDate;
	private String hashTag;
	private String makerName;
	private FileImage repImage;

	public Project toEntity(Member member) {
		return Project.builder()
				.member(member)
				.title(title)
				.summary(summary)
				.story(story)
				.targetAmount(targetAmount)
				.fundingDate(fundingDate)
				.likeCount(0)
				.commentCount(0)
				.views(0)
				.build();
	}
}
