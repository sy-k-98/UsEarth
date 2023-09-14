package com.isfive.usearth.domain.project.entity;

import com.isfive.usearth.domain.common.BaseEntity;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.exception.BusinessException;
import com.isfive.usearth.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE project_comment SET deleted = true WHERE id = ? and version = ?")
@Where(clause = "deleted = false")
public class ProjectComment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_comment_id")
	private ProjectComment projectComment;

	@OneToMany(mappedBy = "projectComment")
	private List<ProjectComment> projectComments = new ArrayList<>();

	private boolean deleted = false;

	@Version
	private Long version;

	public void addReply(ProjectComment reply) {
		if (getProjectComment() != null) {
			throw new RuntimeException("대댓글에는 대댓글을 작성 할 수 없습니다.");
		}
		if (deleted) {
			throw new RuntimeException("삭제된 댓글에는 댓글을 작성 할 수 없습니다.");
		}
		projectComments.add(reply);
		reply.setProjectComment(this);
		project.increaseCommentCount();
	}

	public void setProjectComment(ProjectComment projectComment) {
		this.projectComment = projectComment;
	}
	public void verifyWriter(String username) {
		if (!member.isEqualsUsername(username)) {
			throw new BusinessException(ErrorCode.PROJECT_WRITER_ALLOW);
		}
	}

	public void verifyNotWriter(String username) {
		if (member.isEqualsUsername(username)) {
			throw new BusinessException(ErrorCode.PROJECT_WRITER_NOT_ALLOW);
		}
	}

	public void verifyNotDeleted() {
		if (deleted) {
			throw new BusinessException(ErrorCode.ALREADY_DELETED_PROJECT_COMMENT);
		}
	}

	public Long getParentId() {
		if (getProjectComment() == null) {
			return null;
		}
		return getProjectComment().getId();
	}
}



