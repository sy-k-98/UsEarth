package com.isfive.usearth.domain.project.service;

import com.isfive.usearth.domain.maker.entity.Maker;
import com.isfive.usearth.domain.project.ProjectResponse;
import com.isfive.usearth.domain.project.dto.ProjectRegisterDto;
import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.entity.ProjectFileImage;
import com.isfive.usearth.domain.project.entity.Tag;
import com.isfive.usearth.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

//    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
//    private final MakerRepository makerRepository;
    private final TagService tagService;
    private final FileImageService fileImageService;

    public void createProject(ProjectRegisterDto dto, List<MultipartFile> fileList) {
        Project project = dto.toEntity();
        projectRepository.save(project);

        // 메이커 등록
//        Maker maker = makerRepository.findByName(dto.getMaker());
//        project.setMaker(maker);

        // 태그 리스트 등록
        List<Tag> tagList = tagService.convertTagStrToEntity(dto.getTagList(), project);

        // 이미지 리스트 등록
        List<ProjectFileImage> projectImageList = fileImageService.createProjectFileImageList(fileList, project);

        // 추가사항 저장
        projectRepository.save(project);

    }
    public Page<ProjectResponse> readAllProject(Pageable pageable) {
        Page<Project> projects = projectRepository.findAll(pageable);
        return createProjectResponsePage(pageable, projects);
    }

    private Page<ProjectResponse> createProjectResponsePage(Pageable pageable, Page<Project> projects) {
        List<ProjectResponse> list = projects.stream()
                .map(ProjectResponse::new)
                .toList();
        return new PageImpl<>(list, pageable, projects.getTotalElements());
    }

}
