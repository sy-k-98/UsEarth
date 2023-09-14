package com.isfive.usearth.web.maker.dto.register;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.maker.entity.BusinessInformation;
import com.isfive.usearth.domain.maker.entity.PersonalBusiness;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.web.maker.dto.register_request.BusinessMakerRequest;

public class PersonalRegister extends MakerRegister {
	private String registrationNumber;
	private String corporateName;
	private FileImage registration;

	public PersonalRegister(BusinessMakerRequest request, FileImage profileImage, FileImage submitFile,
		FileImage registration) {
		super(request, profileImage, submitFile);
		this.registrationNumber = request.getRegistrationNumber();
		this.corporateName = request.getCorporateName();
		this.registration = registration;
	}

	public static PersonalRegister createPersonalRegister(
		BusinessMakerRequest request,
		FileImage profileImage,
		FileImage submitFile,
		FileImage registration) {
		return new PersonalRegister(request, profileImage, submitFile, registration);
	}

	public PersonalBusiness toEntity(Member member) {
		return PersonalBusiness.builder()
			.name(this.getName())
			.profileImage(this.getProfileImage().getStoredName())
			.phone(this.getPhone())
			.email(this.getEmail())
			.submitFile(this.getSubmitFile().getStoredName())
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
