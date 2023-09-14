package com.isfive.usearth.domain.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.isfive.usearth.domain.board.entity.Post;
import com.isfive.usearth.domain.common.FileImage;

import lombok.Data;

@Data
public class PostResponse {

	private final Long id;

	private final String title;

	private final String writer;

	private final String content;

	private final Integer views;

	private final Integer likeCount;

	private final LocalDateTime createdDate;

	private List<FileImage> fileImages;

	private boolean likedByUser;

	private Page<PostCommentResponse> postCommentResponse;

	public PostResponse(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.writer = post.getWriterNickname();
		this.content = post.getContent();
		this.views = post.getViews();
		this.likeCount = post.getLikeCount();
		this.createdDate = post.getCreatedDate();
		this.fileImages = post.getFileImages();
		this.likedByUser = false;
	}

	public void setPostCommentResponse(Page<PostCommentResponse> postCommentResponse) {
		this.postCommentResponse = postCommentResponse;
	}
}
