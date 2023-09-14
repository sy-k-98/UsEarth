package com.isfive.usearth.domain.board.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isfive.usearth.domain.board.entity.PostComment;

import lombok.Data;

@Data
public class PostCommentResponse {

	private Long id;
	private String content;
	private String writer;
	private boolean deleted;
	private LocalDateTime createDate;
	private List<PostCommentResponse> postCommentResponses = new ArrayList<>();

	@JsonIgnore
	private Long parentId;

	public PostCommentResponse(PostComment postComment) {
		this.id = postComment.getId();
		this.content = postComment.getContent();
		this.writer = postComment.getMember().getUsername();
		this.deleted = postComment.isDeleted();
		this.createDate = postComment.getCreatedDate();
		this.parentId = postComment.getParentId();
	}

	public void addPostCommentResponses(PostCommentResponse postCommentResponse) {
		postCommentResponses.add(postCommentResponse);
	}

	@JsonIgnore
	public boolean isReply() {
		return parentId != null;
	}
}
