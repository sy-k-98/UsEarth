package com.isfive.usearth.domain.board.repository.post;

import static com.isfive.usearth.domain.board.entity.QPost.*;
import static com.isfive.usearth.domain.member.entity.QMember.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.isfive.usearth.domain.board.entity.Post;
import com.isfive.usearth.exception.EntityNotFoundException;
import com.isfive.usearth.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostQueryRepository {

	private final JPAQueryFactory query;

	@Override
	public Page<Post> findPosts(Long boardId, Pageable pageable) {
		List<Post> posts = query.selectFrom(post)
			.leftJoin(post.member, member).fetchJoin()
			.where(post.board.id.eq(boardId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(post.id.desc())
			.fetch();

		return new PageImpl<>(posts, pageable, posts.size());
	}

	@Override
	public Post findByIdWithMember(Long postId) {
		return Optional.ofNullable(
				query.selectFrom(post)
					.join(post.member, member)
					.where(post.id.eq(postId))
					.fetchOne())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
	}
}
