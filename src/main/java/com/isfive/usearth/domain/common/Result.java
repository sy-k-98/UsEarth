package com.isfive.usearth.domain.common;

import java.util.List;

import lombok.Data;

@Data
public class Result<T> {
	private List<T> data;

	public Result(List<T> data) {
		this.data = data;
	}
}
