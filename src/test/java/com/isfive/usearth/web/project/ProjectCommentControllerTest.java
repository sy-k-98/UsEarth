package com.isfive.usearth.web.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isfive.usearth.domain.maker.entity.Individual;
import com.isfive.usearth.domain.maker.repository.MakerRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.entity.ProjectComment;
import com.isfive.usearth.domain.project.repository.ProjectCommentRepository;
import com.isfive.usearth.domain.project.repository.ProjectRepository;
import com.isfive.usearth.web.project.dto.ProjectCommentCreateRegister;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ProjectCommentControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MakerRepository makerRepository;
    @Autowired
    ProjectCommentRepository projectCommentRepository;

    @WithMockUser(username = "comment writer")
    @DisplayName("사용자는 댓글을 작성할 수 있다.")
    @Test
    void writeComment() throws Exception {
        //given
        Member projectWriter = Member.builder()
                .username("project writer")
                .build();
        Member commentWriter = Member.builder()
                .username("comment writer")
                .build();
        memberRepository.saveAll(List.of(projectWriter, commentWriter));

        Individual individual = Individual.builder().name("개인사업자").build();
        individual.setMember(projectWriter);
        makerRepository.save(individual);

        Project project = Project.builder()
                .title("title")
                .member(projectWriter)
                .maker(individual)
                .commentCount(0)
                .build();
        projectRepository.save(project);

        ProjectCommentCreateRegister request = new ProjectCommentCreateRegister("댓글!");
        //when //then

        mockMvc.perform(post("/projects/{projectId}/comments", project.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(MockMvcResultHandlers.print());

        Project findProject = projectRepository.findById(project.getId()).orElseThrow();
        List<ProjectComment> allComment = projectCommentRepository.findAll();

        assertThat(findProject.getCommentCount()).isEqualTo(1);
        assertThat(allComment.size()).isEqualTo(1);
    }

    @WithMockUser(username = "comment writer")
    @DisplayName("사용자는 대댓글을 작성할 수 있다.")
    @Test
    void writeReply() throws Exception {
        //given
        Member projectWriter = Member.builder()
                .username("project writer")
                .build();
        Member commentWriter = Member.builder()
                .username("comment writer")
                .build();
        memberRepository.saveAll(List.of(projectWriter, commentWriter));

        Individual individual = Individual.builder().name("개인사업자").build();
        individual.setMember(projectWriter);
        makerRepository.save(individual);

        Project project = Project.builder()
                .title("title")
                .member(projectWriter)
                .maker(individual)
                .commentCount(0)
                .build();
        projectRepository.save(project);

        ProjectComment projectComment = ProjectComment.builder()
                .content("댓글")
                .member(commentWriter)
                .project(project)
                .projectComments(new ArrayList<>())
                .build();
        projectCommentRepository.save(projectComment);

        ProjectCommentCreateRegister request = new ProjectCommentCreateRegister("대댓글!");

        //when //then
        mockMvc.perform(post("/project-comments/{commentId}/reply", projectComment.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(MockMvcResultHandlers.print());
        System.out.println(project);
        ProjectComment comment = projectCommentRepository.findById(projectComment.getId()).orElseThrow();
        ProjectComment reply = projectCommentRepository.findById(projectComment.getId() + 1).orElseThrow();

        assertThat(reply.getProjectComment()).isEqualTo(comment);
    }

    @WithMockUser(username = "comment writer")
    @DisplayName("사용자는 댓글을 삭제할 수 있다.")
    @Test
    void deleteReply() throws Exception {
        //given
        Member projectWriter = Member.builder()
                .username("project writer")
                .build();
        Member commentWriter = Member.builder()
                .username("comment writer")
                .build();
        memberRepository.saveAll(List.of(projectWriter, commentWriter));

        Individual individual = Individual.builder().name("개인사업자").build();
        individual.setMember(projectWriter);
        makerRepository.save(individual);

        Project project = Project.builder()
                .title("title")
                .member(projectWriter)
                .maker(individual)
                .commentCount(0)
                .build();
        projectRepository.save(project);

        ProjectComment projectComment = ProjectComment.builder()
                .content("댓글")
                .member(commentWriter)
                .project(project)
                .build();
        projectCommentRepository.save(projectComment);

        //when //then
        mockMvc.perform(delete("/project-comments/{commentId}",  projectComment.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        List<ProjectComment> all = projectCommentRepository.findAll();
        assertThat(all).isEmpty();

    }

}
