package com.isfive.usearth.web.maker.dto.register;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.maker.entity.Individual;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.web.maker.dto.register_request.MakerRegisterRequest;

public class IndividualRegister extends MakerRegister {
	private FileImage idCard;

	private IndividualRegister(MakerRegisterRequest request,
		FileImage profileImage,
		FileImage submitFile,
		FileImage idCard) {
		super(request, profileImage, submitFile);
		this.idCard = idCard;
	}

	public static IndividualRegister createIndividualRegister(MakerRegisterRequest request,
		FileImage profileImage,
		FileImage submitFile,
		FileImage idCard) {
		return new IndividualRegister(request, profileImage, submitFile, idCard);
	}

	public Individual toEntity(Member member) {
		return Individual.builder()
			.name(this.getName())
			.profileImage(this.getProfileImage().getStoredName())
			.phone(this.getPhone())
			.email(this.getEmail())
			.submitFile(this.getSubmitFile().getStoredName())
			.idCard(this.idCard.getStoredName())
			.member(member)
			.build();

	}
}
