package com.isfive.usearth.web.maker.dto.register_request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessMakerRequest extends MakerRegisterRequest {
	@Schema(example = "123-45-67890")
	private String registrationNumber;
	@Schema(example = "기업명")
	private String corporateName;
}
