package com.isfive.usearth.domain.project.entity;

import com.isfive.usearth.domain.common.FileImage;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProjectFileImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private FileImage fileImage;

	@ManyToOne(fetch = FetchType.LAZY)
	private Project project;

	public void setProject(Project project) {
		this.project = project;
		if (!project.getProjectImages().contains(this))
			project.getProjectImages().add(this);
	}
}
