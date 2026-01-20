package com.animalfarm.mlf.domain.user.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.common.JwtProvider;
import com.animalfarm.mlf.common.RedisUtil;
import com.animalfarm.mlf.domain.user.dto.LoginRequestDTO;
import com.animalfarm.mlf.domain.user.dto.TokenResponseDTO;
import com.animalfarm.mlf.domain.user.dto.UserDTO;
import com.animalfarm.mlf.domain.user.repository.UserRepository;

/**
 * [사용자 서비스 클래스]
 * - 스마트팜 STO 프로젝트의 핵심 인증 및 회원 로직을 담당합니다.
 * - 인터페이스 없이 단일 클래스로 작성하여 개발 속도를 최적화했습니다.
 * - PostgreSQL(MyBatis), Redis, JWT 유틸리티를 결합하여 동작합니다.
 */
@Service
public class AuthService {

	// DB 접근을 담당하는 MyBatis 매퍼 인터페이스 (최근 팀장님이 인터페이스 방식을 채택함)
	@Autowired
	private UserRepository userRepository;

	// JWT 생성 및 검증 유틸리티 (AccessToken: 60분, RefreshToken: 30일)
	@Autowired
	private JwtProvider jwtProvider;

	// Redis 데이터 조작 유틸리티 (RT 저장 및 블랙리스트 관리)
	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * [1. 로그인 처리 로직]
	 * - DB에서 사용자를 확인하고, 성공 시 JWT 토큰 세트를 발급합니다.
	 * @param request 사용자가 입력한 이메일과 비밀번호
	 * @return Access Token과 Refresh Token이 담긴 DTO
	 */
	@Transactional(readOnly = true) //DB 조회 시 정합성을 위해 트랜잭션 설정
	public TokenResponseDTO login(LoginRequestDTO request) {

		// 1-1. PostgreSQL DB에서 이메일을 기준으로 사용자 조회
		UserDTO user = userRepository.findByEmail(request.getEmail());

		// 1-2. 사용자 존재 여부 및 비밀번호 일치 확인
		// (참고: 보안을 위해 실무에서는 BCryptPasswordEncoder로 암호화된 비밀번호를 비교해야 합니다.)
		if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("이메일 또는 비밀번호가 틀렸습니다.");
		}

		// 1-3. JWT 토큰 발급
		// Access Token: 60분 유효 (Subject: email)
		String accessToken = jwtProvider.createAccessToken(user.getEmail(), user.getRole());
		// Refresh Token: 30일 유효
		String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

		// 1-4. Redis에 Refresh Token 저장 (Key: "RT:이메일", TTL: 30일)
		// 30일이 지나면 Redis에서 자동으로 삭제되도록 설정되어 있습니다.
		redisUtil.saveRefreshToken(user.getEmail(), refreshToken);

		// 1-5. 클라이언트(프론트)에게 전달할 토큰 정보 반환
		return new TokenResponseDTO(accessToken, refreshToken);

	}

	/**
	 * [2. 액세스 토큰 재발급 로직]
	 * - 만료된 Access Token 대신 리프레시 토큰을 검증하여 새로운 AT를 발급합니다.
	 * @param refreshToken 클라이언트가 보낸 리프레시 토큰
	 * @return 새로운 Access Token (60분 유효)
	 */
	public Map<String, String> refresh(String oldRefreshToken) {

		// 2-1. 전달받은 RT 자체가 JWT로서 유효한지(서명, 만료시간 등) 체크
		if (!jwtProvider.validateToken(oldRefreshToken)) {
			throw new RuntimeException("Refresh 토큰이 만료되었거나 올바르지 않습니다.");
		}

		// 2-2. 토큰에서 사용자의 이메일 정보를 추출
		String email = jwtProvider.getUserEmail(oldRefreshToken);

		// 2-3. DB에서 사용자 권한(Role) 가져오기
		// 사용자님의 Mapper나 Repository를 사용하여 DB 정보를 조회합니다.
		UserDTO user = userRepository.findByEmail(email); // 예시: 이메일로 사용자 조회
		if (user == null) {
			throw new RuntimeException("존재하지 않는 사용자입니다.");
		}
		String role = user.getRole(); // DB에 저장된 사용자의 권한 (예: ADMIN, USER)

		// 2-4. Redis에 저장된 해당 유저의 '진짜' RT를 가져옴
		String saveRt = redisUtil.getData("RT:" + email);

		// 2-5. 보안 핵심: 클라이언트가 보낸 RT와 Redis의 RT가 정확히 일치하는지 대조
		// 토큰 가로채기나 변조가 의심되는 상황
		if (saveRt == null || !saveRt.equals(oldRefreshToken)) {
			throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
		}

		// 2-6. 일치한다면 사용자의 최신 정보를 DB에서 가져와 새로운 60분짜리 Access Token 발급
		// 새로운 AT 및 RT 생성 (가져온 role 사용)
		String newRefreshToken = jwtProvider.createRefreshToken(email);
		String newAccessToken = jwtProvider.createAccessToken(email, role);

		// 2-7. Redis 업데이트 (기존 것 삭제 후 새 것 저장)
		redisUtil.deleteData("RT:" + email);
		redisUtil.saveRefreshToken(email, newRefreshToken);

		// 2-8. 결과 반환
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("accessToken", newAccessToken);
		tokenMap.put("refreshToken", newRefreshToken);

		return tokenMap;
	}

	/**
	 * [3. 로그아웃 처리 로직]
	 * - Redis에서 RT를 즉시 삭제하고, 현재 사용 중인 AT를 블랙리스트에 올립니다.
	 * @param accessToken 로그아웃 요청 시 헤더에 담긴 엑세스 토큰
	 */
	public void logout(String accessToken) {

		// 3-1. 토큰에서 사용자 이메일 추출 후 Redis에서 해당 유저의 RT 삭제
		String email = jwtProvider.getUserEmail(accessToken);
		redisUtil.deleteData("RT:" + email);

		// 3-2. 사용 중이던 Access Token을 블랙리스트에 등록 (중요)
		// 토큰 탈취 시 남은 시간 동안 사용되는 것을 막기 위해 'BL:토큰값' 형태로 Redis에 저장합니다.
		// 현재 AT의 유효시간이 최대 60분이므로, 안전하게 60분 동안 블랙리스트에 보관합니다.
		redisUtil.setBlackList(accessToken, "logout", 60L);

	}

}
