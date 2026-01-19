package com.animalfarm.mlf.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.common.RedisUtil;
import com.animalfarm.mlf.domain.user.dto.SignUpRequestDTO;
import com.animalfarm.mlf.domain.user.dto.UserDTO;
import com.animalfarm.mlf.domain.user.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RedisUtil redisUtil;

	@Autowired
	public UserService(UserRepository userRepository,
		PasswordEncoder passwordEncoder,
		RedisUtil redisUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.redisUtil = redisUtil;
	}

	@Transactional
	public void signUp(SignUpRequestDTO req) {

		// 0) 기본 검증
		if (req == null) {
			throw new IllegalArgumentException("요청이 비어있습니다.");
		}
		if (isBlank(req.getEmail())) {
			throw new IllegalArgumentException("이메일이 필요합니다.");
		}
		if (isBlank(req.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 필요합니다.");
		}
		if (isBlank(req.getUserName())) {
			throw new IllegalArgumentException("이름이 필요합니다.");
		}

		// 1) 이메일 중복 체크
		if (userRepository.findByEmail(req.getEmail()) != null) {
			throw new IllegalArgumentException("이미 가입된 이메일입니다.");
		}

		// 2) 이메일 인증 여부 체크
		String verified = redisUtil.getData(emailVerifiedKey(req.getEmail()));
		if (verified == null) {
			throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
		}

		// 3) 저장 객체 구성
		UserDTO user = new UserDTO();
		user.setEmail(req.getEmail());
		user.setUserName(req.getUserName());
		user.setPassword(passwordEncoder.encode(req.getPassword())); // ✅ BCrypt 해시 저장
		user.setPhoneNumber(req.getPhoneNumber()); // 휴대폰 인증 미구현이면 입력값 그대로

		// 4) 기업/개인 자동 판별
		if (!isBlank(req.getBrn())) {
			user.setBrn(req.getBrn());
			user.setRole("ENTERPRISE");
		} else {
			user.setRole("USER");
		}

		// 5) 사용자에 insert
		userRepository.insertUser(user);

		// 6) 인증 플래그 정리인데 안써도 되지만 혹시 몰라서 넣어놓기
		redisUtil.deleteData(emailVerifiedKey(req.getEmail()));
	}

	private String emailVerifiedKey(String email) {
		return "EMAIL_VERIFIED:" + email;
	}

	private boolean isBlank(String s) {
		// Java 11 기준 null-safe
		return s == null || s.isBlank();
	}
}
