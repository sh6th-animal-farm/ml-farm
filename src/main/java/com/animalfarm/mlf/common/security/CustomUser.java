package com.animalfarm.mlf.common.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.animalfarm.mlf.domain.user.dto.UserDTO;

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

	/**
	 * UserDTO를 직접 받는 생성자 추가
	 */
	public CustomUser(UserDTO dto) {
		// 부모 클래스(User) 생성자 호출: email, password, authorities
		super(dto.getEmail(), dto.getPassword(),
			Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + dto.getRole())));

		this.userId = dto.getUserId(); // DTO에서 PK 추출
		this.userRole = dto.getRole(); // DTO에서 권한 추출
	}
}