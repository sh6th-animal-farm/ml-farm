package com.animalfarm.mlf.common.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.animalfarm.mlf.domain.user.dto.UserDTO;

/**
 * [포인트]
 * 스프링 시큐리티 기본 User 클래스를 상속받음
 * 스프링 시큐리티의 기본 객체는 부서장님의 DB 테이블 구조를 전혀 모른다 -> CustomUser 만들어주기
 * 마리팜 DB의 user_id를 보관할 필드(userId) 추가
 */
public class CustomUser extends User {

	private final Long userId;
	private final String userName;
	private final String role;

	public CustomUser(UserDTO user) {
		// 부모 클래스 생성자: 이메일, 비밀번호, 권한(ROLE_ 접두사 추가) 세팅 (기본 상속)
		super(
			user.getEmail(),
			user.getPassword(),
			Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));

		//DB에서 가져온 user_id, user_name, role를 이 객체에 고정함
		this.userId = user.getUserId();
		this.userName = user.getUserName();
		this.role = user.getRole();
	}

	public Long getUserId() {
		return userId;
	}

	public String getRole() {
		return role;
	}

	public String getUserName() {
		return userName;
	}
}
