package com.isfive.usearth.domain.project.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.exception.EntityNotFoundException;
import com.isfive.usearth.exception.ErrorCode;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	Page<Project> findAll(Pageable pageable);

	default Project findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.PROJECT_NOT_FOUND));
	}

	@Query("select distinct p from Project p join fetch p.rewards r " +
		"where p.id = :projectId")
	Optional<Project> findByIdWithRewards(@Param("projectId") Long projectId);
}
