package com.isfive.usearth.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.entity.ProjectLike;

public interface ProjectLikeRepository extends JpaRepository<ProjectLike, Long> {

    List<ProjectLike> findByMember_UsernameAndProjectIn(String username, List<Project> projects);

    Optional<ProjectLike> findByProjectAndMember(Project project, Member member);

    boolean existsByProject_IdAndMember_Username(Long projectId, String username);
}
