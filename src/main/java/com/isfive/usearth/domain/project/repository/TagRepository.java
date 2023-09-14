package com.isfive.usearth.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isfive.usearth.domain.project.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
