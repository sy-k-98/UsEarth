package com.isfive.usearth.domain.maker.entity;

import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.exception.BusinessException;
import com.isfive.usearth.exception.ErrorCode;
import com.isfive.usearth.web.maker.dto.MakerUpdate;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public class Maker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String profileImage;

	private String email;

	private String phone;

	private String submitFile;

	private LocalDateTime deletedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public Maker(String name, String profileImage, String email, String phone, String submitFile, Member member) {
		this.name = name;
		this.profileImage = profileImage;
		this.phone = phone;
		this.email = email;
		this.submitFile = submitFile;
		this.member = member;
	}

	public void update(MakerUpdate makerUpdate) {
		if (makerUpdate.getPhone() != null) {
			this.phone = makerUpdate.getPhone();
		}
		if (makerUpdate.getEmail() != null) {
			this.email = makerUpdate.getEmail();
		}
	}

	public void delete() {
		deletedAt = LocalDateTime.now();
	}

	@OneToMany(mappedBy = "maker")
	private List<Project> projects = new ArrayList<>();

	public void addProject(Project project) {
		this.getProjects().add(project);
		if (project.getMaker() != this)
			project.setMaker(this);
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public void verify(String username) {
		if (!member.isEqualsUsername(username)) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
		}
	}
}
