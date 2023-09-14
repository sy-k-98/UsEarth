package com.isfive.usearth.web.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectCommentCreateRegister {

    @Schema(example = "댓글 내용")
    @NotBlank(message =  "댓글 내용은 필수입니다.")
    private String content;

    public ProjectCommentCreateRegister(String content) {
        this.content = content;
    }
}
