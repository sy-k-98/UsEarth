package com.isfive.usearth.domain.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.Period;
import com.isfive.usearth.domain.common.Result;
import com.isfive.usearth.domain.maker.entity.Individual;
import com.isfive.usearth.domain.maker.repository.MakerRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.domain.project.dto.ProjectCreate;
import com.isfive.usearth.domain.project.dto.RewardCreate;
import com.isfive.usearth.domain.project.dto.RewardsResponse;
import com.isfive.usearth.domain.project.entity.*;
import com.isfive.usearth.domain.project.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProjectServiceTest {

    @Autowired ProjectService projectService;
    @Autowired MemberRepository memberRepository;
    @Autowired MakerRepository makerRepository;
    @Autowired ProjectRepository projectRepository;
    @Autowired RewardRepository rewardRepository;
    @Autowired OptionRepository optionRepository;
    @Autowired OptionValueRepository optionValueRepository;
    @Autowired RewardSkuRepository rewardSkuRepository;
    @Autowired SkuValueRepository skuValueRepository;
    @Autowired TagRepository tagRepository;
    @Autowired ProjectFileImageRepository projectFileImageRepository;

    @Autowired RewardService rewardService;
    @Autowired EntityManager em;


    @DisplayName("사용자는 프로젝트를 생성할 수 있다.")
    @Test
    void createProject() {
        //given

        Member member = Member.builder().username("username").build();
        memberRepository.save(member);
        Individual individual = Individual.builder().name("Individual").member(member).build();
        makerRepository.save(individual);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Period period = Period.builder()
                .startDate(LocalDate.parse("2023-08-22", formatter))
                .dueDate(LocalDate.parse("2023-12-31", formatter))
                .build();

        ProjectCreate projectCreate = ProjectCreate.builder()
                .title("프로젝트 이름")
                .summary("요약")
                .story("스토리")
                .targetAmount(10000)
                .fundingDate(period)
                .hashTag("\"#상의 #하의 #반팔\"")
                .makerName("Individual")
                .build();

        List<RewardCreate> rewardCreates = new ArrayList<>();

        Map<String, String> options1 = new HashMap<>();
        options1.put("색상", "검정,빨강,흰색");
        options1.put("사이즈", "S,M,L");

        Map<String, Integer> optionStock1 = new HashMap<>();
        optionStock1.put("검정,S", 5);
        optionStock1.put("검정,M", 6);
        optionStock1.put("검정,L", 6);
        optionStock1.put("빨강,S", 10);
        optionStock1.put("빨강,M", 10);
        optionStock1.put("빨강,L", 10);
        optionStock1.put("흰색,S", 5);
        optionStock1.put("흰색,M", 5);
        optionStock1.put("흰색,L", 5);

        rewardCreates.add(RewardCreate.builder()
                .title("리워드이름")
                .description("리워드설명")
                .price(10000)
                .deliveryFee(3000)
                .expectedSendDate("2023-12-31")
                .options(options1)
                .optionStocks(optionStock1)
                .build());

        Map<String, String> options2 = new HashMap<>();
        options2.put("색상", "민트,핑크");
        options2.put("사이즈", "S,M,L");

        Map<String, Integer> optionStock2 = new HashMap<>();
        optionStock2.put("민트,S", 15);
        optionStock2.put("민트,M", 15);
        optionStock2.put("민트,L", 10);
        optionStock2.put("핑크,S", 7);
        optionStock2.put("핑크,M", 7);
        optionStock2.put("핑크,L", 7);

        rewardCreates.add(RewardCreate.builder()
                .title("리워드이름2")
                .description("리워드설명2")
                .price(30000)
                .deliveryFee(3000)
                .expectedSendDate("2023-12-31")
                .options(options2)
                .optionStocks(optionStock2)
                .build());

        List<FileImage> fileImages = new ArrayList<>();
        fileImages.add(FileImage.builder().originalName("original1").storedName("stored1").build());
        fileImages.add(FileImage.builder().originalName("original2").storedName("stored2").build());

        //when
        projectService.createProject("username", projectCreate, rewardCreates, fileImages);
        //then

        List<Project> projects = projectRepository.findAll();
        List<Reward> rewards = rewardRepository.findAll();
        List<Option> options = optionRepository.findAll();
        List<OptionValue> optionValues = optionValueRepository.findAll();
        List<RewardSku> rewardSkus = rewardSkuRepository.findAll();
        List<SkuValue> skuValues = skuValueRepository.findAll();
        List<Tag> tags = tagRepository.findAll();
        List<ProjectFileImage> projectFileImages = projectFileImageRepository.findAll();

        assertThat(projects.size()).isEqualTo(1);
        assertThat(rewards.size()).isEqualTo(2);
        assertThat(options.size()).isEqualTo(4);
        assertThat(optionValues.size()).isEqualTo(11);
        assertThat(rewardSkus.size()).isEqualTo(15);
        assertThat(skuValues.size()).isEqualTo(30);
        assertThat(tags.size()).isEqualTo(3);
        assertThat(projectFileImages.size()).isEqualTo(2);
    }
}