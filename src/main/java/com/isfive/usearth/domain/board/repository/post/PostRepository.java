package com.isfive.usearth.domain.board.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isfive.usearth.domain.board.entity.Post;
import com.isfive.usearth.exception.EntityNotFoundException;
import com.isfive.usearth.exception.ErrorCode;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {

	default Post findByIdOrThrow(Long id) {
		return findById(id).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
	}

	;
}
