package com.animalfarm.mlf.common.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
		// e.getMessage()에는 우리가 깎아놓은 "보유 토큰이 부족합니다"가 들어있음
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
			.body(ApiResponse.message(e.getMessage())); // JSON으로 응답
	}
}
