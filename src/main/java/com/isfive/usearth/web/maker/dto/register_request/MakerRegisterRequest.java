package com.isfive.usearth.web.maker.dto.register_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MakerRegisterRequest {
	@Schema(example = "메이커이름")
	@NotBlank(message = "메이커명을 입력해주세요.")
	private String name;

	@Schema(example = "maker@email.com")
	@NotBlank(message = "이메일을 입력해주세요.")
	private String email;

	@Schema(example = "010-1111-1111")
	@NotBlank(message = "전화번호를 입력해주세요.")
	private String phone;

}
