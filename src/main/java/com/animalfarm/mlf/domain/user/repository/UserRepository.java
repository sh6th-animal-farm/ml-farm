package com.animalfarm.mlf.domain.user.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.user.dto.UserDTO;

/**
 * [사용자 레파지토리 매퍼]
 * @Mapper 어노테이션이 있으면 MyBatis가 이 인터페이스의 구현체를 자동으로 생성합니다.
 * 더 이상 UserRepositoryImpl 클래스를 직접 만들 필요가 없습니다!
 */
@Mapper
public interface UserRepository {

	/**
	 * [이메일로 사용자 조회]
	 * 메서드 이름은 XML의 id와 반드시 일치해야 합니다.
	 */
	UserDTO findByEmail(String email);

	int insertUser(UserDTO user);

	boolean existsByEmail(String email);

	String selectAddress(Long userId);

	int insertAddress(@Param("address")
	String address, @Param("userId")
	Long userId);
}
