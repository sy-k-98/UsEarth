package com.isfive.usearth.web.maker;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.FileImageService;
import com.isfive.usearth.domain.maker.dto.MakerResponse;
import com.isfive.usearth.domain.maker.service.MakerService;
import com.isfive.usearth.web.maker.dto.MakerUpdate;
import com.isfive.usearth.web.maker.dto.MakerUpdateRequest;
import com.isfive.usearth.web.maker.dto.register.CorporateRegister;
import com.isfive.usearth.web.maker.dto.register.IndividualRegister;
import com.isfive.usearth.web.maker.dto.register.PersonalRegister;
import com.isfive.usearth.web.maker.dto.register_request.BusinessMakerRequest;
import com.isfive.usearth.web.maker.dto.register_request.MakerRegisterRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/makers")
@RequiredArgsConstructor
@Tag(name = "2. Maker", description = "Maker API")
public class MakerController {

	private final MakerService makerService;
	private final FileImageService fileImageService;

	@Operation(summary = "메이커(개인) 등록")
	@PostMapping(path = "/individual", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createIndividual(
			Authentication auth,
			@RequestPart("MakerRegisterRequest") MakerRegisterRequest request,
			@RequestPart("profileImage") MultipartFile profileImage,
			@RequestPart("submitFile") MultipartFile submitFile,
			@RequestPart("idCard") MultipartFile idCard
	) {
		FileImage savedprofileImage = fileImageService.createFileImage(profileImage);
		FileImage savedSubmitFile = fileImageService.createFileImage(submitFile);
		FileImage savedIdCard = fileImageService.createFileImage(idCard);

		IndividualRegister register = IndividualRegister.createIndividualRegister(
				request,
				savedprofileImage,
				savedSubmitFile,
				savedIdCard);

		makerService.createIndividualBy(auth.getName(), register);

		return new ResponseEntity<>("메이커 신청이 완료되었습니다.", HttpStatus.CREATED);
	}

	@Operation(summary = "메이커(개인 사업자) 등록")
	@PostMapping(path = "/personal-business", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createPersonalBusiness(
			Authentication auth,
			@RequestPart("BusinessMakerRequest") BusinessMakerRequest request,
			@RequestPart("profileImage") MultipartFile profileImage,
			@RequestPart("submitFile") MultipartFile submitFile,
			@RequestPart("registration") MultipartFile registration
	) {
		FileImage savedprofileImage = fileImageService.createFileImage(profileImage);
		FileImage savedSubmitFile = fileImageService.createFileImage(submitFile);
		FileImage savedRegistration = fileImageService.createFileImage(registration);

		PersonalRegister register = PersonalRegister.createPersonalRegister(
				request,
				savedprofileImage,
				savedSubmitFile,
				savedRegistration);
		makerService.createPersonalBusinessBy(auth.getName(), register);

		return new ResponseEntity<>("메이커 신청이 완료되었습니다.", HttpStatus.CREATED);
	}

	@Operation(summary = "메이커(법인 사업자) 등록")
	@PostMapping(path = "/corporate-business", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createCorporateBusiness(
			Authentication auth,
			@RequestPart("BusinessMakerRequest") BusinessMakerRequest request,
			@RequestPart("profileImage") MultipartFile profileImage,
			@RequestPart("submitFile") MultipartFile submitFile,
			@RequestPart("registration") MultipartFile registration,
			@RequestPart("corporateSealCertificate") MultipartFile corporateSealCertificate
	) {
		FileImage savedprofileImage = fileImageService.createFileImage(profileImage);
		FileImage savedSubmitFile = fileImageService.createFileImage(submitFile);
		FileImage savedRegistration = fileImageService.createFileImage(registration);
		FileImage savedCorporateSealCertificate = fileImageService.createFileImage(corporateSealCertificate);

		CorporateRegister register = CorporateRegister.createCorporateRegister(
				request,
				savedprofileImage,
				savedSubmitFile,
				savedRegistration,
				savedCorporateSealCertificate);
		makerService.createCorporateBusinessBy(auth.getName(), register);

		return new ResponseEntity<>("메이커 신청이 완료되었습니다.", HttpStatus.CREATED);
	}

	@Operation(summary = "메이커 정보 확인")
	@GetMapping("/{makerId}")
	public ResponseEntity<MakerResponse> getMaker(
			@PathVariable("makerId") Long id
	) {
		MakerResponse response = makerService.readMakerById(id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "메이커 수정")
	@PutMapping("/{makerId}")
	public ResponseEntity<MakerResponse> updateMaker(
			Authentication auth,
			@PathVariable("makerId") Long id,
			@RequestBody MakerUpdateRequest request
	) {
		MakerUpdate makerUpdate = MakerUpdate.toMakerUpdate(request);
		MakerResponse response = makerService.updateMakerById(auth.getName(), id, makerUpdate);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "메이커 삭제")
	@DeleteMapping("/{makerId}")
	public ResponseEntity<String> deleteMaker(
			Authentication auth,
			@PathVariable("makerId") Long id
	) {
		makerService.removeMakerById(auth.getName(), id);
		return new ResponseEntity<>("메이커가 삭제되었습니다.", HttpStatus.CREATED);
	}

}
