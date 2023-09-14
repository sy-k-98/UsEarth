package com.isfive.usearth.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.entity.ProjectFileImage;

public interface ProjectFileImageRepository extends JpaRepository<ProjectFileImage, Long> {
	List<ProjectFileImage> findAllByProject(Project project);
}
