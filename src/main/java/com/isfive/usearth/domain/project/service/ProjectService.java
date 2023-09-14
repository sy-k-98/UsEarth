package com.isfive.usearth.domain.project.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isfive.usearth.annotation.FilesDelete;
import com.isfive.usearth.annotation.Retry;
import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.maker.entity.Maker;
import com.isfive.usearth.domain.maker.repository.MakerRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.domain.project.dto.ProjectCommentResponse;
import com.isfive.usearth.domain.project.dto.ProjectCreate;
import com.isfive.usearth.domain.project.dto.ProjectResponse;
import com.isfive.usearth.domain.project.dto.ProjectUpdate;
import com.isfive.usearth.domain.project.dto.ProjectsResponse;
import com.isfive.usearth.domain.project.dto.RewardCreate;
import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.entity.ProjectFileImage;
import com.isfive.usearth.domain.project.entity.ProjectLike;
import com.isfive.usearth.domain.project.entity.Reward;
import com.isfive.usearth.domain.project.entity.Tag;
import com.isfive.usearth.domain.project.repository.ProjectFileImageRepository;
import com.isfive.usearth.domain.project.repository.ProjectLikeRepository;
import com.isfive.usearth.domain.project.repository.ProjectRepository;
import com.isfive.usearth.domain.project.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

	private final MemberRepository memberRepository;
	private final ProjectRepository projectRepository;
	private final MakerRepository makerRepository;
	private final TagRepository tagRepository;
	private final ProjectFileImageRepository projectFileImageRepository;
	private final ProjectLikeRepository projectLikeRepository;
	private final RewardService rewardService;
	private final TagService tagService;
	private final ProjectCommentService projectCommentService;

	@FilesDelete
	@Transactional
	public ProjectResponse createProject(String username, ProjectCreate projectCreate,
		List<RewardCreate> rewardCreateList, List<FileImage> fileList) {
		Member member = memberRepository.findByUsernameOrThrow(username);

		Project project = projectCreate.toEntity(member);
		projectRepository.save(project);

		// 대표 이미지 등록
		project.setRepImage(projectCreate.getRepImage());

		// 메이커 등록
		Maker maker = makerRepository.findByName(projectCreate.getMakerName());
		maker.addProject(project);

		// 태그 리스트 등록
		List<Tag> tagList = tagService.convertTagStrToEntity(tagService.createTagList(projectCreate.getHashTag()));
		for (Tag tag : tagList)
			project.addTag(tagRepository.save(tag));

		// 이미지 리스트 등록
		List<ProjectFileImage> projectImageList = createProjectFileImageList(fileList);
		for (ProjectFileImage image : projectImageList)
			project.addProjectFileImage(image);

		// 리워드 등록
		List<Reward> rewardList = rewardService.createReward(rewardCreateList);
		for (Reward reward : rewardList)
			project.addReward(reward);

		// 추가사항 저장
		projectRepository.save(project);

		return new ProjectResponse(project);
	}

	@FilesDelete
	@Transactional
	public ProjectResponse updateProject(String username, Long projectId, ProjectUpdate projectUpdate, List<FileImage> fileList) {
		Member member = memberRepository.findByUsernameOrThrow(username);

		Project project = projectRepository.findByIdOrElseThrow(projectId);
		project.verifyWriter(username);

		// 프로젝트 정보 수정
		project.update(projectUpdate.toEntity());

		// 대표 이미지 수정
		project.setRepImage(projectUpdate.getRepImage());

		// 이미지 리스트 수정
		List<ProjectFileImage> oldImageList = projectFileImageRepository.findAllByProject(project);
		List<ProjectFileImage> newImageList = createProjectFileImageList(fileList);
		projectFileImageRepository.deleteAll(oldImageList);

		project.getProjectImages().clear();
		for (ProjectFileImage image : newImageList)
			project.addProjectFileImage(image);

		// 변경사항 저장
		projectRepository.save(project);

		return new ProjectResponse(project);
	}

	@Transactional
	public void deleteProject(String username, Long projectId) {
		Member member = memberRepository.findByUsernameOrThrow(username);
		Project project = projectRepository.findByIdOrElseThrow(projectId);
		project.verifyNotDeleted();
		project.verifyWriter(username);

		projectRepository.delete(project);
	}

	public Page<ProjectsResponse> readProjects(Integer page, String username) {
		PageRequest pageRequest = PageRequest.of(page - 1, 10);
		Page<Project> projects = projectRepository.findAll(pageRequest);
		List<ProjectsResponse> projectsResponses = createProjectResponse(projects);
		List<ProjectLike> projectLikes = projectLikeRepository.findByMember_UsernameAndProjectIn(username, projects.getContent());
		Set<Long> projectIdSet = createProjectIdSetBy(projectLikes);
		setLikedByUser(projectsResponses, projectIdSet);

		return new PageImpl<>(projectsResponses, pageRequest, projects.getTotalElements());
	}

	@Transactional
	public ProjectResponse readProject(Long projectId, String username) {
		Project project = projectRepository.findByIdOrElseThrow(projectId);
		project.increaseView();

		ProjectResponse projectResponse = new ProjectResponse(project);

		boolean likedByUser = projectLikeRepository.existsByProject_IdAndMember_Username(projectId, username);
		projectResponse.setLikedByUser(likedByUser);

		Page<ProjectCommentResponse> projectCommentResponses = projectCommentService.findComments(projectId, 1);
		projectResponse.setProjectCommentResponses(projectCommentResponses);

		return projectResponse;
	}

	@Retry
	@Transactional
	public void like(Long projectId, String username) {
		Project project = projectRepository.findByIdOrElseThrow(projectId);
		project.verifyNotWriter(username);

		Member member = memberRepository.findByUsernameOrThrow(username);
		Optional<ProjectLike> optionalProjectLike = projectLikeRepository.findByProjectAndMember(project, member);

		if (optionalProjectLike.isPresent()) {
			cancelLike(project, optionalProjectLike.get());
		} else {
			like(project, member);
		}
	}

	private List<ProjectFileImage> createProjectFileImageList(List<FileImage> fileImageList) {
		List<ProjectFileImage> projectFileImageList = new ArrayList<>();
		for (FileImage file : fileImageList) {
			projectFileImageList.add(createProjectFileImage(file));
		}
		projectFileImageRepository.saveAll(projectFileImageList);
		return projectFileImageList;
	}

	private ProjectFileImage createProjectFileImage(FileImage fileImage) {
		ProjectFileImage projectFileImage = ProjectFileImage.builder()
			.fileImage(fileImage)
			.build();
		projectFileImageRepository.save(projectFileImage);
		return projectFileImage;
	}

	private List<ProjectsResponse> createProjectResponse(Page<Project> projects) {
		return projects.stream()
			.map(ProjectsResponse::new)
			.toList();
	}

	private Set<Long> createProjectIdSetBy(List<ProjectLike> projectLikes) {
		Set<Long> projectIdSet = new HashSet<>();
		projectLikes.forEach(projectLike ->
				projectIdSet.add(projectLike.getProject().getId()));
		return projectIdSet;
	}

	private void setLikedByUser(List<ProjectsResponse> projectsResponses, Set<Long> projectIdSet) {
		projectsResponses.forEach(projectsResponse -> {
			if (projectIdSet.contains(projectsResponse.getId())) {
				projectsResponse.setLikedByUser(true);
			}
		});
	}

	private void cancelLike(Project project, ProjectLike projectLike) {
		projectLikeRepository.delete(projectLike);
		project.cancelLike();
	}

	private void like(Project project, Member member) {
		ProjectLike projectLike = new ProjectLike(member, project);
		projectLikeRepository.save(projectLike);
		project.increaseLikeCount();
	}
}
