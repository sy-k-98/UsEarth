package com.isfive.usearth.domain.project.service;

import com.isfive.usearth.annotation.Retry;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.domain.project.dto.ProjectCommentResponse;
import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.entity.ProjectComment;
import com.isfive.usearth.domain.project.repository.ProjectCommentRepository;
import com.isfive.usearth.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectCommentService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectCommentRepository projectCommentRepository;

    @Retry
    @Transactional
    public void createComment(Long projectId, String content, String username) {
        Project project = projectRepository.findByIdOrElseThrow(projectId);
        project.verifyNotWriter(username);

        Member member = memberRepository.findByUsernameOrThrow(username);

        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .content(content)
                .build();
        projectCommentRepository.save(projectComment);
        project.increaseCommentCount();
    }

    @Retry
    @Transactional
    public void createReply(Long commentId, String content, String username) {
        ProjectComment projectComment = projectCommentRepository.findByIdOrElseThrow(commentId);

        Member member = memberRepository.findByUsernameOrThrow(username);
        ProjectComment reply = ProjectComment.builder()
                .member(member)
                .project(projectComment.getProject())
                .content(content)
                .build();
        projectComment.addReply(reply);
        projectCommentRepository.save(reply);
    }

    @Transactional
    public void deleteComment(Long commentId, String username) {
        ProjectComment projectComment = projectCommentRepository.findByIdOrElseThrow(commentId);
        projectComment.verifyNotDeleted();
        projectComment.verifyWriter(username);
        projectCommentRepository.delete(projectComment);
        projectComment.getProject().decreaseCommentCount();
    }

    public Page<ProjectCommentResponse> findComments(Long projectId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 20);
        Page<ProjectComment> projectComments = projectCommentRepository.findAllByProject_Id(projectId, pageRequest);

        Map<Long, ProjectCommentResponse> map = createResponseMap(projectComments);

        insertReply(map);

        List<ProjectCommentResponse> list = createCommentList(map);
        return new PageImpl<>(list, pageRequest, list.size());
    }

    private Map<Long, ProjectCommentResponse> createResponseMap(Page<ProjectComment> projectComments) {
        return projectComments.getContent()
                .stream()
                .collect(toMap(ProjectComment::getId, ProjectCommentResponse::new));
    }

    private void insertReply(Map<Long, ProjectCommentResponse> map) {
        map.values().forEach(projectCommentResponse -> {
            if (projectCommentResponse.getParentId() != null) {
                ProjectCommentResponse parent = map.get(projectCommentResponse.getParentId());
                parent.addProjectCommentResponses(projectCommentResponse);
            }
        });
    }

    private List<ProjectCommentResponse> createCommentList(Map<Long, ProjectCommentResponse> map) {
        return map.values().stream()
                .filter(projectCommentResponse -> !projectCommentResponse.isReply())
                .sorted(Comparator.comparingLong(ProjectCommentResponse::getId).reversed())
                .collect(toList());
    }
}
