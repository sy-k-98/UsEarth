package com.isfive.usearth.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isfive.usearth.domain.project.entity.ProjectComment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectCommentResponse {

    private Long id;
    private String content;
    private String writer;
    private boolean deleted;
    private LocalDateTime createDate;
    private List<ProjectCommentResponse> projectCommentResponses = new ArrayList<>();

    @JsonIgnore
    private Long parentId;

    public ProjectCommentResponse(ProjectComment projectComment) {
        this.id = projectComment.getId();
        this.content = projectComment.getContent();
        this.writer = projectComment.getMember().getUsername();
        this.deleted = projectComment.isDeleted();
        this.createDate = projectComment.getCreatedDate();
        this.parentId = projectComment.getParentId();
    }

    public void addProjectCommentResponses(ProjectCommentResponse projectCommentResponse) {
        projectCommentResponses.add(projectCommentResponse);
    }

    @JsonIgnore
    public boolean isReply() {
        return parentId != null;
    }
}
