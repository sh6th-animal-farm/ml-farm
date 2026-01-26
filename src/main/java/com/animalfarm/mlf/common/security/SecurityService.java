package com.animalfarm.mlf.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.user.dto.UserDTO;
import com.animalfarm.mlf.domain.user.repository.UserRepository;

/**
 * [회원 관리 서비스]
 * - 회원 정보 수정, 탈퇴 등 '데이터' 중심의 로직을 담당합니다.
 * - 스프링 시큐리티의 UserDetailsService를 구현하여 인증 데이터를 공급합니다.
 */
@Service
public class SecurityService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * [시큐리티 전용: 유저 정보 조회]
	 * - JwtProvider가 인증 객체를 만들 때 DB에서 유저 정보를 가져오는 통로입니다.
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserDTO user = userRepository.findByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다.");
		}

		return new CustomUser(user);

	}

}
