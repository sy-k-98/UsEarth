package com.isfive.usearth.web.board;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.isfive.usearth.domain.board.service.PostCommentService;
import com.isfive.usearth.web.board.dto.PostCommentCreateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "5. Post", description = "Post API")
public class PostCommentController {

	private final PostCommentService postCommentService;

	@Operation(summary = "게시글 댓글 등록")
	@PostMapping("/posts/{postId}/comments")
	public ResponseEntity<Void> writeComment(Authentication auth,
		@PathVariable("postId") Long postId,
		@RequestBody @Valid PostCommentCreateRequest request) {
		postCommentService.createComment(postId, request.getContent(), auth.getName());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Operation(summary = "게시글 대댓글 등록")
	@PostMapping("/post-comments/{commentId}/reply")
	public ResponseEntity<Void> writeReply(Authentication auth,
		@PathVariable("commentId") Long commentId,
		@RequestBody @Valid PostCommentCreateRequest request) {
		postCommentService.createReply(commentId, request.getContent(), auth.getName());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Operation(summary = "게시글 댓글 삭제")
	@DeleteMapping("/post-comments/{commentId}")
	public ResponseEntity<Void> deleteComment(Authentication auth, @PathVariable("commentId") Long commentId) {
		postCommentService.deleteComment(commentId, auth.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
