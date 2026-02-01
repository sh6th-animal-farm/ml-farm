package com.animalfarm.mlf.domain.user.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.user.dto.UserCertificateLinkDTO;
import com.animalfarm.mlf.domain.user.dto.UserDTO;

@Mapper
public interface UserRepository {
	
	UserDTO findByEmail(String email);

	int insertUser(UserDTO user);

	boolean existsByEmail(String email);

	String selectAddress(Long userId);

	int updateAddress(@Param("address")
	String address, @Param("userId")
	Long userId);

	UserDTO getUserById(Long userId);

	Long selectUserIdByUclId(Long walletId);
}
