package com.isfive.usearth.domain.board.dto;

import java.time.LocalDateTime;

import com.isfive.usearth.domain.board.entity.Post;

import lombok.Data;

@Data
public class PostsResponse {

	private final Long id;

	private final String title;

	private final String writer;

	private final Integer views;

	private final Integer likeCount;

	private final Integer commentCount;

	private final LocalDateTime createdDate;

	private Boolean likedByUser;

	public PostsResponse(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.writer = post.getWriterNickname();
		this.views = post.getViews();
		this.likeCount = post.getLikeCount();
		this.commentCount = post.getCommentCount();
		this.createdDate = post.getCreatedDate();
		this.likedByUser = false;
	}
}
