package com.isfive.usearth.web.funding.dto;

import com.isfive.usearth.domain.funding.dto.DeliveryRegister;
import com.isfive.usearth.domain.funding.entity.Address;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {

	@Schema(example = "홍길동")
	@NotBlank(message = "이름 입력해야 합니다.")
	private String name;

	@Schema(example = "010-1234-5678")
	@Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}", message = "전화 번호는 000-0000-0000 형식이어야 합니다.")
	private String phone;

	@NotNull(message = "주소를 입력해야 합니다.")
	private Address address;

	public DeliveryRegister toServiceDto() {
		return new DeliveryRegister(name, phone, address);
	}
}
