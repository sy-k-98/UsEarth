package com.isfive.usearth.domain.board.repository;

import com.isfive.usearth.domain.board.entity.Board;
import com.isfive.usearth.exception.EntityNotFoundException;
import com.isfive.usearth.exception.ErrorCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isfive.usearth.domain.board.entity.Post;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p " +
            "join fetch p.member m " +
            "where p.board.id = :boardId " +
            "order by p.id desc ")
    Page<Post> findAllByBoard_IdOrderByIdDesc(@Param("boardId") Long boardId, Pageable pageable);

    @Query("select p from Post p where p.id = :postId")
    Optional<Post> findByIdWithMember(@Param("postId") Long postId);

    default Post findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
    };
}
