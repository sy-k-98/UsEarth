package com.isfive.usearth.domain.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isfive.usearth.domain.board.entity.Post;
import com.isfive.usearth.domain.board.entity.PostLike;
import com.isfive.usearth.domain.member.entity.Member;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	List<PostLike> findByMember_UsernameAndPostIn(String username, List<Post> posts);

	Optional<PostLike> findByPostAndMember(Post post, Member member);

	boolean existsByPost_IdAndMember_Email(Long postId, String email);
}
