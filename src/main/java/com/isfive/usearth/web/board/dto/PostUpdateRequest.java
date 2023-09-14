package com.isfive.usearth.web.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

	@Size(max = 100, message = "게시글 제목을 100자 이상으로 할 수 없습니다.")
	@NotBlank(message = "게시글 제목은 필수 입니다.")
	private String title;

	@NotBlank(message = "게시글 내용은 필수 입니다.")
	private String content;

	@Builder
	private PostUpdateRequest(String title, String content) {
		this.title = title;
		this.content = content;
	}
}
