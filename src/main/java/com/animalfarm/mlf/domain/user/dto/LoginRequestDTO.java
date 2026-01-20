package com.animalfarm.mlf.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [로그인 요청 DTO]
 * 클라이언트(프론트엔드)가 로그인 시 전송하는 JSON 데이터를 담는 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

	/**
	 * [사용자 이메일]
	 * 로그인 아이디로 사용됩니다.
	 */
	private String email;

	/**
	 * [사용자 비밀번호]
	 * DB의 암호화된 비밀번호와 대조할 원문 비밀번호입니다.
	 */
	private String password;

}
