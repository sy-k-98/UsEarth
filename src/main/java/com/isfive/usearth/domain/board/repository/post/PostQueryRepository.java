package com.isfive.usearth.domain.board.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.isfive.usearth.domain.board.entity.Post;

public interface PostQueryRepository {

	Page<Post> findPosts(Long boardId, Pageable pageable);

	Post findByIdWithMember(Long postId);
}
