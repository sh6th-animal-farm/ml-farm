package com.animalfarm.mlf.common.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class CustomUser extends User {

	private static final long serialVersionUID = 1L;

	private Long userId; // 마리팜 DB PK
	private String userRole; // 유저 권한 (기존 userClass에서 변경)

	/**
	 * 매개변수를 직접 받는 원본 스타일 생성자
	 */
	public CustomUser(String username, String password,
		Collection<? extends GrantedAuthority> authorities,
		Long userId, String userRole) {
		//username = 이메일을 ID로 사용하
		super(username, password, authorities);
		this.userId = userId;
		this.userRole = userRole;
	}
}