package com.isfive.usearth.web.funding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isfive.usearth.domain.funding.dto.DeliveryRegister;
import com.isfive.usearth.domain.funding.dto.PaymentRegister;
import com.isfive.usearth.domain.funding.entity.*;
import com.isfive.usearth.domain.funding.repository.FundingRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.entity.Reward;
import com.isfive.usearth.domain.project.entity.RewardSku;
import com.isfive.usearth.domain.project.repository.ProjectRepository;
import com.isfive.usearth.domain.project.repository.RewardRepository;
import com.isfive.usearth.domain.project.repository.RewardSkuRepository;
import com.isfive.usearth.web.funding.dto.DeliveryRequest;
import com.isfive.usearth.web.funding.dto.FundingRequest;
import com.isfive.usearth.web.funding.dto.PaymentRequest;
import com.isfive.usearth.web.funding.dto.RewardSkuRequest;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class FundingControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired MemberRepository memberRepository;
    @Autowired RewardRepository rewardRepository;
    @Autowired RewardSkuRepository rewardSkuRepository;
    @Autowired FundingRepository fundingRepository;
    @Autowired ProjectRepository projectRepository;

    @WithMockUser(username = "member")
    @DisplayName("사용자는 펀딩을 할 수있다.")
    @Test
    void funding() throws Exception {
        //given
        memberRepository.save(Member.builder().username("member").build());

        Project project = Project.builder().title("프로젝트").totalFundingAmount(0).targetAmount(1000).build();
        projectRepository.save(project);

        Reward reward = Reward.builder().price(10000).project(project).build();
        rewardRepository.save(reward);
        RewardSku rewardSku1 = RewardSku.builder().reward(reward).stock(5).initStock(5).build();
        RewardSku rewardSku2 = RewardSku.builder().reward(reward).stock(10).initStock(10).build();
        rewardSkuRepository.saveAll(List.of(rewardSku1, rewardSku2));

        Address address = new Address("서울", "1번지");
        DeliveryRequest deliveryRequest = new DeliveryRequest("name", "010-0000-0000", address);
        RewardSkuRequest rewardSkuRequest1 = new RewardSkuRequest(rewardSku1.getId(), 2);
        RewardSkuRequest rewardSkuRequest2 = new RewardSkuRequest(rewardSku2.getId(), 5);
        PaymentRequest paymentRequest = new PaymentRequest("0000-0000-0000-0000", "2025-05", "991111", "12");
        FundingRequest fundingRequest = new FundingRequest(deliveryRequest, paymentRequest, List.of(rewardSkuRequest1, rewardSkuRequest2));

        //when //then
        mockMvc.perform(post("/projects/{projectsId}/funding", project.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundingRequest))
                )
                .andExpect(status().isOk())
                .andDo(print());

        List<Funding> all = fundingRepository.findAll();
        assertThat(all).hasSize(1);
        Funding funding = all.get(0);
        assertThat(funding.getDelivery())
                .extracting("address.detail", "address.zipcode", "name", "phone")
                .contains("서울", "1번지", "name", "010-0000-0000");

        assertThat(funding.getPayment())
                .extracting("cardNumber", "cardExpiry", "birth", "pwd_2digit")
                .contains("0000-0000-0000-0000", "2025-05", "991111", "12");

        List<FundingRewardSku> fundingRewardSkus = funding.getFundingRewardSkus();
        assertThat(fundingRewardSkus).hasSize(2);
        assertThat(fundingRewardSkus.get(0))
                .extracting("rewardSku", "funding", "count", "price")
                .contains(rewardSku1, funding, 2, 10000);
        assertThat(fundingRewardSkus.get(1))
                .extracting("rewardSku", "funding", "count", "price")
                .contains(rewardSku2, funding, 5, 10000);

        //재고 감소 확인
        RewardSku findRewardSku1 = rewardSkuRepository.findById(rewardSku1.getId()).orElseThrow();
        RewardSku findRewardSku2 = rewardSkuRepository.findById(rewardSku2.getId()).orElseThrow();
        assertThat(findRewardSku1.getStock()).isEqualTo(3);
        assertThat(findRewardSku2.getStock()).isEqualTo(5);

        Project findProject = projectRepository.findById(project.getId()).orElseThrow();
        assertThat(findProject.getTotalFundingAmount()).isEqualTo(70000);
    }

    @WithMockUser(username = "member")
    @DisplayName("사용자는 펀딩을 취소할 수있다.")
    @Test
    void cancel() throws Exception {
        //given
        Member member = Member.builder().username("member").build();
        memberRepository.save(member);

        Project project = Project.builder().title("프로젝트").totalFundingAmount(200000).targetAmount(300000).build();
        projectRepository.save(project);

        Reward reward1 = Reward.builder().title("리워드1").project(project).build();
        Reward reward2 = Reward.builder().title("리워드2").project(project).build();
        rewardRepository.saveAll(List.of(reward1, reward2));

        Address address = new Address("서울", "1번지");
        Delivery delivery = Delivery.createDelivery(new DeliveryRegister("name", "010-0000-0000", address));
        Payment payment = Payment.createPayment(new PaymentRegister("0000-0000-0000-0000", "2025-05", "991111", "12"));

        RewardSku rewardSku1 = RewardSku.builder().reward(reward1).stock(10).initStock(10).build();
        RewardSku rewardSku2 = RewardSku.builder().reward(reward2).stock(20).initStock(20).build();
        rewardSkuRepository.saveAll(List.of(rewardSku1, rewardSku2));

        FundingRewardSku fundingRewardSku1 = FundingRewardSku.createFundingRewardSku(rewardSku1, 5, 10000);
        FundingRewardSku fundingRewardSku2 = FundingRewardSku.createFundingRewardSku(rewardSku2, 5, 10000);
        Funding funding = Funding.createFunding(member, delivery, payment, List.of(fundingRewardSku1, fundingRewardSku2));
        fundingRepository.save(funding);

        //when //then
        mockMvc.perform(delete("/projects/{projectsId}/funding/{fundingId}", project.getId(), funding.getId())
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());

        Funding findFunding = fundingRepository.findById(funding.getId()).orElseThrow();
        assertThat(findFunding.getStatus()).isEqualTo(FundingStatus.CANCEL);

        RewardSku findRewardSku1 = rewardSkuRepository.findById(rewardSku1.getId()).orElseThrow();
        RewardSku findRewardSku2 = rewardSkuRepository.findById(rewardSku2.getId()).orElseThrow();
        assertThat(findRewardSku1.getStock()).isEqualTo(10);
        assertThat(findRewardSku2.getStock()).isEqualTo(20);

        Project findProject = projectRepository.findById(project.getId()).orElseThrow();
        assertThat(findProject.getTotalFundingAmount()).isEqualTo(100000);
    }
}