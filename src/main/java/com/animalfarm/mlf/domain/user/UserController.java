package com.animalfarm.mlf.domain.user;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.user.dto.LoginRequestDTO;
import com.animalfarm.mlf.domain.user.dto.TokenResponseDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * [사용자 인증 컨트롤러]
 * - 스마트팜 STO 서비스의 로그인, 로그아웃, 토큰 재발급을 담당합니다.
 * - Swagger 2를 사용하여 API 문서를 자동 생성합니다.
 */
@RestController
@RequestMapping("/api/user")
@Api(tags = "User Authentication API", description = "로그인 및 토큰 관리 API")
public class UserController {

	@Autowired
	private UserService userService;

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

		// 1-1. 서비스 계층에 로그인 요청 위임
		TokenResponseDTO tokenResponse = userService.login(loginRequest);

		// 1-2. 성공 시 200 OK와 함께 토큰 전송
		return ResponseEntity.ok(tokenResponse);
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

		String oldRefreshToken = requestBody.get("refreshToken");

		// 2-1. 서비스 계층에서 Redis 검증 후 AT, RT가 담긴 Map을 받아옴
		Map<String, String> newTokens = userService.refresh(oldRefreshToken);

		// 2-2. 결과를 그대로 응답 (JSON에 AT와 RT가 모두 포함됨)
		return ResponseEntity.ok(newTokens);

	}

	/**
	 * [3. 로그아웃 API]
	 * - 사용자의 세션을 종료하고 토큰을 무효화합니다.
	 */
	@PostMapping(value = "/logout", produces = "application/json; charset=UTF-8")
	@ApiOperation(value = "로그아웃", notes = "리프레시 토큰을 삭제하고 엑세스 토큰을 블랙리스트에 등록합니다.")
	public ResponseEntity<?> logout(HttpServletRequest request) {

		// 3-1. HTTP 헤더에서 Authorization (Bearer Token) 추출
		String authHeader = request.getHeader("Authorization");
		String accessToken = null; // "Bearer " 이후의 토큰값만 추출

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			accessToken = authHeader.substring(7);
		}

		if (accessToken != null) {
			// 3-2. 서비스 계층에서 Redis 데이터 처리 (RT 삭제 및 AT 블랙리스트)
			userService.logout(accessToken);
			return ResponseEntity.ok("로그아웃 되었습니다.");
		}

		return ResponseEntity.badRequest().body("유효하지 않은 요청입니다.");

	}

}
