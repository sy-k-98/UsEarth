package com.isfive.usearth.domain.funding.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.isfive.usearth.domain.funding.entity.Funding;
import com.isfive.usearth.domain.funding.entity.FundingRewardSku;
import com.isfive.usearth.domain.funding.entity.FundingStatus;
import com.isfive.usearth.domain.funding.repository.FundingRepository;
import com.isfive.usearth.domain.project.entity.Project;
import com.isfive.usearth.domain.project.repository.ProjectRepository;
import com.isfive.usearth.domain.utils.mail.MailService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

	private final MailService mailService;
	private final FundingRepository fundingRepository;
	private final ProjectRepository projectRepository;

	@Value("${iamport.api.key}")
	private String API_KEY;

	@Value("${iamport.api.secret}")
	private String API_SECRET;

	private static String extractPaymentFail(String errorMessage) {
		int startIndex1 = errorMessage.indexOf("[");
		String paymentFail = errorMessage.substring(0, startIndex1 - 1);

		int startIndex2 = errorMessage.indexOf("pgMessage=");
		int endIndex = errorMessage.indexOf(",", startIndex2);
		String error = errorMessage.substring(startIndex2 + "pgMessage=".length(), endIndex);

		String result = paymentFail + "\n" + "<" + error + "> 문제가 발생하였으니 결제 정보 입력값을 다시 확인해주세요.";
		return result;
	}

	//    @Scheduled(cron = "0/30 * * * * ?")
	@Scheduled(cron = "0 0 0 * * ?")
	@Transactional
	public void findProject() throws Exception {
		List<Project> projects = projectRepository.findAll();
		for (Project project : projects) {
			LocalDate nextDay = project.getFundingDate().getDueDate().plus(1, ChronoUnit.DAYS);
			if (LocalDate.now().isEqual(nextDay) && project.isAchieve()) {
				createPayment(project);
			}
		}
	}

	private void createPayment(Project project) throws Exception {
		List<Funding> fundingList = fundingRepository.findByFundingRewardSkus_RewardSku_Reward_Project(project);

		for (Funding funding : fundingList) {
			if (funding.getStatus().equals(FundingStatus.ORDER)) {
				Response response = payment(funding);
				// 예외처리
				if (response.getCode() == 0)
					funding.setStatus(FundingStatus.PAYMENT);
				else {
					funding.setStatus(FundingStatus.REJECT);
					String errorMessage = extractPaymentFail(response.getMessage());
					String receiver = funding.getMember().getEmail();
					mailService.sendErrorEmail(receiver, errorMessage);
				}
			}
		}
	}

	private Response payment(Funding funding) {

		IamportClient client = new IamportClient(API_KEY, API_SECRET);
		String token = "";

		try {
			token = client.getAuth().getResponse().getToken();
		} catch (IOException | IamportResponseException e) {
			throw new RuntimeException(e);
		}

		String paymentURL = "https://api.iamport.kr/subscribe/payments/onetime";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);

		Double amount = funding.getFundingRewardSkus().stream().mapToDouble(FundingRewardSku::getAmount).sum();
		String name = funding.getFundingRewardSkus()
				.stream()
				.map(FundingRewardSku::getRewardName)
				.collect(Collectors.joining(", "));

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("merchant_uid", "FUNDING_ID_" + LocalDateTime.now());
		jsonObject.addProperty("amount", amount);
		jsonObject.addProperty("card_number", funding.getPayment().getCardNumber());
		jsonObject.addProperty("expiry", funding.getPayment().getCardExpiry());
		jsonObject.addProperty("birth", funding.getPayment().getBirth());
		jsonObject.addProperty("pwd_2digit", funding.getPayment().getPwd_2digit());
		jsonObject.addProperty("pg", "iamport01m");
		jsonObject.addProperty("name", name);

		Gson gson = new Gson();
		String jsonRequest = gson.toJson(jsonObject);
		HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

		return restTemplate.postForObject(paymentURL, entity, Response.class);
	}

	@Data
	private static class Response {
		Integer code;
		String message;
		Map<String, Object> response;
	}

}
