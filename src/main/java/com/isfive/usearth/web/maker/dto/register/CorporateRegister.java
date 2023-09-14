package com.isfive.usearth.web.maker.dto.register;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.maker.entity.BusinessInformation;
import com.isfive.usearth.domain.maker.entity.CorporateBusiness;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.web.maker.dto.register_request.BusinessMakerRequest;

public class CorporateRegister extends MakerRegister {
	private String registrationNumber;
	private String corporateName;
	private FileImage registration;
	private FileImage corporateSealCertificate;

	private CorporateRegister(BusinessMakerRequest request,
		FileImage profileImage,
		FileImage submitFile,
		FileImage registration,
		FileImage corporateSealCertificate) {
		super(request, profileImage, submitFile);
		this.registrationNumber = request.getRegistrationNumber();
		this.corporateName = request.getCorporateName();
		this.registration = registration;
		this.corporateSealCertificate = corporateSealCertificate;
	}

	public static CorporateRegister createCorporateRegister(BusinessMakerRequest request, FileImage profileImage,
		FileImage submitFile, FileImage registration, FileImage corporateSealCertificate) {
		return new CorporateRegister(request, profileImage, submitFile, registration, corporateSealCertificate);
	}

	public CorporateBusiness toEntity(Member member) {
		return CorporateBusiness.builder()
			.name(this.getName())
			.profileImage(this.getProfileImage().getStoredName())
			.phone(this.getPhone())
			.email(this.getEmail())
			.submitFile(this.getSubmitFile().getStoredName())
			.corporateSealCertificate(this.corporateSealCertificate.getStoredName())
			.businessInformation(
				BusinessInformation.builder()
					.corporateName(this.corporateName)
					.registrationNumber(this.registrationNumber)
					.registration(this.registration.getStoredName())
					.build()
			)
			.member(member)
			.build();
	}
}
