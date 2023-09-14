package com.isfive.usearth.domain.funding.dto;

import com.isfive.usearth.domain.funding.entity.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryRegister {
	private String name;
	private String phone;
	private Address address;
}
