package com.isfive.usearth.domain.board.service;

import static java.util.stream.Collectors.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isfive.usearth.annotation.Retry;
import com.isfive.usearth.domain.board.dto.PostCommentResponse;
import com.isfive.usearth.domain.board.entity.Post;
import com.isfive.usearth.domain.board.entity.PostComment;
import com.isfive.usearth.domain.board.repository.PostCommentRepository;
import com.isfive.usearth.domain.board.repository.post.PostRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostCommentService {

	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final PostCommentRepository postCommentRepository;

	@Retry
	@Transactional
	public void createComment(Long postId, String content, String username) {
		Post post = postRepository.findByIdWithMember(postId);
		post.verifyNotWriter(username);

		Member member = memberRepository.findByUsernameOrThrow(username);

		PostComment postComment = PostComment.createPostComment(member, post, content);
		postCommentRepository.save(postComment);
		post.increaseCommentCount();
	}

	@Retry
	@Transactional
	public void createReply(Long commentId, String content, String username) {
		PostComment postComment = postCommentRepository.findByIdWithPost(commentId).orElseThrow();

		Member member = memberRepository.findByUsernameOrThrow(username);
		PostComment reply = PostComment.createPostComment(member, postComment.getPost(), content);
		postComment.addReply(reply);
		postCommentRepository.save(reply);
	}

	@Transactional
	public void deleteComment(Long commentId, String username) {
		PostComment postComment = postCommentRepository.findByIdWithMember(commentId).orElseThrow();
		postComment.verifyWriter(username);
		postComment.delete();
	}

	/**
	 * 댓글들을 모두 조회한다.(대댓글 포함)
	 * 대댓글DTO를 댓글DTO에 끼워 넣는다.
	 * 댓글DTO만 List로 만들어 반환한다. (대댓글을 댓글DTO 파라미터에 포함)
	 */
	public Page<PostCommentResponse> findComments(Long postId, Integer page) {
		PageRequest pageRequest = PageRequest.of(page - 1, 20);
		Page<PostComment> postComments = postCommentRepository.findAllByPost_Id(postId, pageRequest);

		Map<Long, PostCommentResponse> map = createResponseMap(postComments);

		insertReply(map);

		List<PostCommentResponse> list = createCommentList(map);
		return new PageImpl<>(list, pageRequest, list.size());
	}

	private Map<Long, PostCommentResponse> createResponseMap(Page<PostComment> postComments) {
		return postComments.getContent()
			.stream()
			.collect(toMap(PostComment::getId, PostCommentResponse::new));
	}

	private void insertReply(Map<Long, PostCommentResponse> map) {
		map.values().forEach(postCommentResponse -> {
			if (postCommentResponse.getParentId() != null) {
				PostCommentResponse parent = map.get(postCommentResponse.getParentId());
				parent.addPostCommentResponses(postCommentResponse);
			}
		});
	}

	private List<PostCommentResponse> createCommentList(Map<Long, PostCommentResponse> map) {
		return map.values().stream()
			.filter(postCommentResponse -> !postCommentResponse.isReply())
			.sorted(Comparator.comparingLong(PostCommentResponse::getId).reversed())
			.collect(toList());
	}

}
