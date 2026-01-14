package com.animalfarm.mlf.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * [사용자 서비스 클래스]
 * 인터페이스 없이 단일 클래스로 구성하여 유지보수 속도를 높였습니다.
 * PostgreSQL(MyBatis), Redis, JWT 유틸리티를 결합하여 인증 로직을 처리합니다.
 */
@Service
public class UserService {

	// MyBatis Mapper: PostgreSQL DB 접근을 담당
	@Autowired
	private UserMapper userMapper;
}
