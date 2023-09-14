package com.isfive.usearth.web.project;

import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.domain.project.entity.*;
import com.isfive.usearth.domain.project.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RewardControllerTest {

    @Autowired MemberRepository memberRepository;
    @Autowired ProjectRepository projectRepository;
    @Autowired RewardRepository rewardRepository;
    @Autowired OptionRepository optionRepository;
    @Autowired OptionValueRepository optionValueRepository;
    @Autowired RewardSkuRepository rewardSkuRepository;
    @Autowired SkuValueRepository skuValueRepository;
    @Autowired EntityManager em;
    @Autowired MockMvc mockMvc;

    @WithMockUser(username = "username")
    @DisplayName("사용자는 리워드 목록을 조회 할 수 있다.")
    @Test
    void findRewards() throws Exception {

        Member member = Member.builder().username("username").build();
        memberRepository.save(member);

        Project project = Project.builder().title("프로젝트").build();
        projectRepository.save(project);

        Reward reward1 = Reward.builder().title("리워드1").project(project).build();
        Reward reward2 = Reward.builder().title("리워드2").project(project).build();
        rewardRepository.saveAll(List.of(reward1, reward2));

        Option option1 = Option.builder().name("옵션1").reward(reward1).build();
        Option option2 = Option.builder().name("옵션2").reward(reward1).build();
        Option option3 = Option.builder().name("옵션3").reward(reward2).build();
        optionRepository.saveAll(List.of(option1, option2, option3));

        OptionValue optionValue1 = OptionValue.builder().value("검정").option(option1).build();
        OptionValue optionValue2 = OptionValue.builder().value("빨강").option(option1).build();
        OptionValue optionValue3 = OptionValue.builder().value("240").option(option2).build();
        OptionValue optionValue4 = OptionValue.builder().value("250").option(option2).build();
        OptionValue optionValue5 = OptionValue.builder().value("S").option(option3).build();
        OptionValue optionValue6 = OptionValue.builder().value("M").option(option3).build();
        optionValueRepository.saveAll(List.of(optionValue1, optionValue2, optionValue3, optionValue4, optionValue5, optionValue6));

        RewardSku rewardSku1 = RewardSku.builder().reward(reward1).stock(10).initStock(10).build();
        RewardSku rewardSku2 = RewardSku.builder().reward(reward1).stock(11).initStock(11).build();
        RewardSku rewardSku3 = RewardSku.builder().reward(reward1).stock(12).initStock(12).build();
        RewardSku rewardSku4 = RewardSku.builder().reward(reward1).stock(13).initStock(13).build();
        RewardSku rewardSku5 = RewardSku.builder().reward(reward2).stock(14).initStock(14).build();
        RewardSku rewardSku6 = RewardSku.builder().reward(reward2).stock(15).initStock(15).build();
        rewardSkuRepository.saveAll(List.of(rewardSku1, rewardSku2, rewardSku3, rewardSku4, rewardSku5, rewardSku6));

        skuValueRepository.save(SkuValue.builder().optionValue(optionValue3).rewardSku(rewardSku1).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue1).rewardSku(rewardSku2).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue4).rewardSku(rewardSku2).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue2).rewardSku(rewardSku3).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue3).rewardSku(rewardSku3).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue2).rewardSku(rewardSku4).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue4).rewardSku(rewardSku4).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue5).rewardSku(rewardSku5).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue6).rewardSku(rewardSku5).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue5).rewardSku(rewardSku6).build());
        skuValueRepository.save(SkuValue.builder().optionValue(optionValue6).rewardSku(rewardSku6).build());

        mockMvc.perform(get("/projects/{projectId}/rewards", project.getId()))
                .andExpect(jsonPath("data.size()").value(2))
                .andExpect(jsonPath("data[0].id").value(reward1.getId()))
                .andExpect(jsonPath("data[1].id").value(reward2.getId()))
                .andExpect(jsonPath("data[0].stock").value(46))
                .andExpect(jsonPath("data[1].stock").value(29))
                .andExpect(jsonPath("data[0].rewardSkuResponses.size()").value(4))
                .andExpect(jsonPath("data[1].rewardSkuResponses.size()").value(2))
                .andDo(MockMvcResultHandlers.print());
    }
}