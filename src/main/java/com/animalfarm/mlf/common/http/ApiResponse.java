package com.animalfarm.mlf.common.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
	private String message;
	private T payload;
	
	// 데이터 없이 메시지만 보내는 응답
	public static <T> ApiResponse<T> message(String message) {
		return new ApiResponse<>(message, null);
	}

	// 데이터를 포함한 응답
	public static <T> ApiResponse<T> messageWithData(String message, T data) {
		return new ApiResponse<>(message, data);
	}
}

