package com.isfive.usearth.web.project.dto;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.project.dto.ProjectUpdate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModify {

	@Schema(example = "수정할 프로젝트 제목")
	@NotBlank(message = "수정할 프로젝트 제목을 입력해야 합니다.")
	private String title;

	@Schema(example = "수정할 프로젝트 요약")
	@NotBlank(message = "수정할 프로젝트 요약을 입력해야 합니다.")
	private String summary;

	public ProjectUpdate toService(FileImage file) {
		return ProjectUpdate.builder()
			.title(title)
			.summary(summary)
			.repImage(file)
			.build();
	}
}
