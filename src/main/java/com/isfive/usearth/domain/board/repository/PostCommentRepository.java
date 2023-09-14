package com.isfive.usearth.domain.board.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.isfive.usearth.domain.board.entity.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

	@Query("select pc from PostComment pc join fetch pc.post where pc.id = :id")
	Optional<PostComment> findByIdWithPost(@Param("id") Long id);

	@Query("select pc from PostComment pc join fetch pc.member where pc.id = :id")
	Optional<PostComment> findByIdWithMember(@Param("id") Long commentId);

	@Query("select pc from PostComment pc join fetch pc.member where pc.post.id = :postId")
	Page<PostComment> findAllByPost_Id(@Param("postId") Long postId, Pageable pageable);
}
