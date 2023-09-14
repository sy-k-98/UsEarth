package com.isfive.usearth.web.maker.dto.register;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.web.maker.dto.register_request.MakerRegisterRequest;

import lombok.Getter;

@Getter
public class MakerRegister {
	private String name;
	private FileImage profileImage;
	private String email;
	private String phone;
	private FileImage submitFile;

	protected MakerRegister(MakerRegisterRequest request, FileImage profileImage, FileImage submitFile) {
		this.name = request.getName();
		this.profileImage = profileImage;
		this.email = request.getEmail();
		this.phone = request.getPhone();
		this.submitFile = submitFile;
	}
}
