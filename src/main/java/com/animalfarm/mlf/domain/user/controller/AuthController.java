package com.animalfarm.mlf.domain.user.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.user.dto.LoginRequestDTO;
import com.animalfarm.mlf.domain.user.dto.TokenResponseDTO;
import com.animalfarm.mlf.domain.user.service.AuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * [사용자 인증 컨트롤러]
 * - 스마트팜 STO 서비스의 로그인, 로그아웃, 토큰 재발급을 담당합니다.
 * - Swagger 2를 사용하여 API 문서를 자동 생성합니다.
 * - Fetch API와의 통신을 위해 모든 응답을 JSON으로 처리합니다.
 */
@RestController
@RequestMapping("/api/auth")
@Api(tags = "User Authentication API", description = "로그인 및 토큰 관리 API")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthService authService; // @Autowired 대신 final 사용 권장

	/**
	 * [1. 로그인 API]
	 * @param loginRequest 이메일과 비밀번호
	 * @return Access Token(60분) 및 Refresh Token(30일)
	 */
	@PostMapping("/login")
	@ApiOperation(value = "사용자 로그인", notes = "이메일과 비밀번호를 받아 JWT 토큰을 발급합니다.")
	public ResponseEntity<?> login(
		@ApiParam(value = "로그인 정보", required = true)
		@RequestBody
		LoginRequestDTO loginRequest) {

		try {
			log.info("로그인 요청: {}", loginRequest.getEmail());
			// 1-1. 서비스 계층에 로그인 요청 위임
			TokenResponseDTO tokenResponse = authService.login(loginRequest);
			// 1-2. 성공 시 200 OK와 함께 토큰 전송
			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {
			log.error("로그인 실패: {}", e.getMessage());
			return ResponseEntity.status(401).body(Map.of("message", "이메일 또는 비밀번호가 틀렸습니다."));
		}
	}

	/**
	 * [2. 토큰 재발급 API]
	 * - 만료된 Access Token을 갱신하기 위해 사용합니다.
	 * @param requestBody 클라이언트가 보낸 JSON { "refreshToken": "..." }
	 */
	@PostMapping("/refresh")
	@ApiOperation(value = "토큰 재발급", notes = "Refresh Token을 이용하여 새로운 Access Token과 Refresh Token을 발급받습니다.")
	public ResponseEntity<?> refresh(@RequestBody
	Map<String, String> requestBody) {
		try {
			String oldRefreshToken = requestBody.get("refreshToken");
			if (!StringUtils.hasText(oldRefreshToken)) {
				return ResponseEntity.badRequest().body(Map.of("message", "리프레시 토큰이 없습니다."));
			}
			// 2-1. 서비스 계층에서 Redis 검증 후 AT, RT가 담긴 Map을 받아옴
			Map<String, String> newTokens = authService.refresh(oldRefreshToken);
			// 2-2. 결과를 그대로 응답 (JSON에 AT와 RT가 모두 포함됨)
			return ResponseEntity.ok(newTokens);
		} catch (Exception e) {
			log.error("토큰 재발급 실패: {}", e.getMessage());
			return ResponseEntity.status(401).body(Map.of("message", "세션이 만료되었습니다. 다시 로그인해주세요."));
		}

	}

	/**
	 * [3. 로그아웃 API]
	 * - 사용자의 세션을 종료하고 토큰을 무효화합니다.
	 * - Access Token을 블랙리스트에 등록하고 Redis에서 RT를 삭제합니다.
	 */
	@PostMapping(value = "/logout", produces = "application/json; charset=UTF-8")
	@ApiOperation(value = "로그아웃", notes = "리프레시 토큰을 삭제하고 엑세스 토큰을 블랙리스트에 등록합니다.")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		try {
			// 3-1. HTTP 헤더에서 Authorization (Bearer Token) 추출
			String bearerToken = request.getHeader("Authorization");
			if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
				String accessToken = bearerToken.substring(7);
				// 3-2. 서비스 계층에서 Redis 데이터 처리 (RT 삭제 및 AT 블랙리스트)
				authService.logout(accessToken);
				return ResponseEntity.ok(Map.of("message", "정상적으로 로그아웃되었습니다."));
			}
			return ResponseEntity.badRequest().body(Map.of("message", "잘못된 로그아웃 요청입니다."));
		} catch (Exception e) {
			log.error("로그아웃 실패: {}", e.getMessage());
			return ResponseEntity.status(500).body(Map.of("message", "로그아웃 처리 중 오류가 발생했습니다."));
		}
	}

}
