package com.isfive.usearth.web.project.dto;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.Period;
import com.isfive.usearth.domain.project.dto.ProjectCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRegister {

	@Schema(example = "프로젝트 제목")
	@NotBlank(message = "프로젝트 제목을 입력해야 합니다.")
	private String title;

	@Schema(example = "프로젝트 요약")
	@NotBlank(message = "프로젝트 요약을 입력해야 합니다.")
	private String summary;

	@Schema(example = "프로젝트 내용")
	@NotBlank(message = "프로젝트 내용을 입력해야 합니다.")
	private String story;

	@Schema(example = "1000000")
	@NotNull(message = "프로젝트 목표금액을 입력해야 합니다.")
	private Integer targetAmount;

	@Schema(example = "2023-09-15")
	@NotBlank(message = "프로젝트 시작 날짜를 입력해야 합니다.")
	private String startDate;

	@Schema(example = "2023-12-31")
	@NotBlank(message = "프로젝트 종료 날짜를 입력해야 합니다.")
	private String endDate;

	@Schema(example = "메이커 이름")
	@NotBlank(message = "메이커 이름을 입력해야 합니다. 메이커는 이미 등록된 메이커만 가능합니다.")
	private String makerName;

	@Schema(example = "#상의 #하의 #반팔")
	@NotBlank(message = "프로젝트 해시태그를 입력해야 합니다. 예시) #상의 #하의 #반팔")
	private String hashTag;

	@Valid
	private List<RewardRegister> rewardRegisterList;

	public ProjectCreate toService(FileImage file) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Period period = Period.builder()
			.startDate(LocalDate.parse(startDate, formatter))
			.dueDate(LocalDate.parse(endDate, formatter))
			.build();

		return ProjectCreate.builder()
			.title(title)
			.summary(summary)
			.story(story)
			.targetAmount(targetAmount)
			.fundingDate(period)
			.makerName(makerName)
			.hashTag(hashTag)
			.repImage(file)
			.build();
	}
}
