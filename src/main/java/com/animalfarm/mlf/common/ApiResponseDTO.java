package com.animalfarm.mlf.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApiResponseDTO<T> {

	private String message;
	private T payload;
}
