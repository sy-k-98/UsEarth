package com.isfive.usearth.domain.board.entity;

import java.util.ArrayList;
import java.util.List;

import com.isfive.usearth.domain.common.BaseEntity;
import com.isfive.usearth.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostComment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_comment_id")
	private PostComment postComment;

	@OneToMany(mappedBy = "postComment")
	private List<PostComment> postComments = new ArrayList<>();

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private boolean deleted;

	@Builder
	private PostComment(Member member, Post post, String content, boolean deleted) {
		this.member = member;
		this.post = post;
		this.content = content;
		this.deleted = deleted;
	}

	public static PostComment createPostComment(Member member, Post post, String content) {
		return PostComment.builder()
			.member(member)
			.post(post)
			.content(content)
			.deleted(false)
			.build();
	}

	public Long getParentId() {
		if (getPostComment() == null) {
			return null;
		}
		return getPostComment().getId();
	}

	public void addReply(PostComment reply) {
		if (getPostComment() != null) {
			throw new RuntimeException("대댓글에는 대댓글을 작성 할 수 없습니다.");
		}
		if (deleted) {
			throw new RuntimeException("삭제된 댓글에는 댓글을 작성 할 수 없습니다.");
		}
		postComments.add(reply);
		reply.setPostComment(this);
		post.increaseCommentCount();
	}

	public void setPostComment(PostComment postComment) {
		this.postComment = postComment;
	}

	public void delete() {
		if (deleted) {
			throw new RuntimeException("이미 삭제된 댓글 입니다.");
		}
		deleted = true;
	}

	public void verifyWriter(String username) {
		if (!member.isEqualsUsername(username)) {
			throw new RuntimeException("댓글 작성자가 아닙니다.");
		}
	}
}
