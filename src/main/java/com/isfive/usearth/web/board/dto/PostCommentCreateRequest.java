package com.isfive.usearth.web.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCommentCreateRequest {

	@Schema(example = "댓글 내용")
	@NotBlank(message = "댓글 내용은 필수 입니다.")
	private String content;

	public PostCommentCreateRequest(String content) {
		this.content = content;
	}
}
