package com.isfive.usearth.web.project;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.isfive.usearth.domain.project.service.ProjectCommentService;
import com.isfive.usearth.web.project.dto.ProjectCommentCreateRegister;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "3. Project", description = "Project API")
public class ProjectCommentController {

    private final ProjectCommentService projectCommentService;

    @Operation(summary = "프로젝트 댓글 등록")
    @PostMapping("/projects/{projectId}/comments")
    public ResponseEntity<Void> writeComment(Authentication auth,
                                             @PathVariable("projectId") Long projectId,
                                             @RequestBody @Valid ProjectCommentCreateRegister request) {
        projectCommentService.createComment(projectId, request.getContent(), auth.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 대댓글 등록")
    @PostMapping("/project-comments/{commentId}/reply")
    public ResponseEntity<Void> writeReply(Authentication auth,
                                           @PathVariable("commentId") Long commentId,
                                           @RequestBody @Valid ProjectCommentCreateRegister request) {
        projectCommentService.createReply(commentId, request.getContent(), auth.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "프로젝트 댓글 삭제")
    @DeleteMapping("/project-comments/{commentId}")
    public ResponseEntity<Void> deleteComment(Authentication auth,
                                              @PathVariable("commentId") Long commentId) {
        projectCommentService.deleteComment(commentId, auth.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
