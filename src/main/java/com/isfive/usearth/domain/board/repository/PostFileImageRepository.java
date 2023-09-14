package com.isfive.usearth.domain.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isfive.usearth.domain.board.entity.PostFileImage;

public interface PostFileImageRepository extends JpaRepository<PostFileImage, Long> {

    List<PostFileImage> findAllByPost_Id(Long postId);
}
