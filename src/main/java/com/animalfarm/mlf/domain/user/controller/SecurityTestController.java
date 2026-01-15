package com.animalfarm.mlf.domain.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/test")
@Api(tags = "Security Test API", description = "권한별 접근 제어 테스트용")
public class SecurityTestController {

	@ApiOperation(value = "로그인 인증 테스트", notes = "로그인한 모든 사용자가 접근 가능합니다.")
	@GetMapping(value = "/auth", produces = "application/json; charset=UTF-8")
	public String userTest() {
		return "인증된 사용자(개인/기업/관리자)만 볼 수 있는 데이터입니다.";
	}

	@ApiOperation(value = "탄소 마켓 접근 테스트", notes = "ENTERPRISE 권한을 가진 기업 투자자만 접근 가능합니다.")
	@GetMapping(value = "/carbon", produces = "application/json; charset=UTF-8")
	public String carbonTest() { // 메서드 명도 의미 있게 바꾸면 좋습니다.
		return "기업 회원 전용 탄소 마켓 테스트 데이터 응답 성공!";
	}

	@ApiOperation(value = "관리자 테스트", notes = "ADMIN 권한을 가진 관리자만 접근 가능합니다.")
	@GetMapping(value = "/admin", produces = "application/json; charset=UTF-8")
	public String adminTest() {
		return "관리자만 볼 수 있는 시스템 관리 비밀 데이터입니다.";
	}

	@GetMapping(value = "/debug", produces = "application/json; charset=UTF-8")
	public String debug() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			return "보안 컨텍스트에 인증 정보가 없습니다! (필터 작동 안 함)";
		}

		if (!auth.isAuthenticated()) {
			return "인증되지 않은 사용자입니다.";
		}

		return "현재 유저: " + auth.getName() + " | 권한: " + auth.getAuthorities().toString();
	}
}