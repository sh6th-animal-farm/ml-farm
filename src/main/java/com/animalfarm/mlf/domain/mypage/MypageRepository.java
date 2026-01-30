package com.animalfarm.mlf.domain.mypage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.mypage.dto.CarbonHistoryDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileUpdateRequestDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProjectDTO;

@Mapper
public interface MypageRepository {

	List<CarbonHistoryDTO> selectCarbonHistoryByUserId(Long userId);

	ProfileDTO selectProfile(Long userId);

	int updateProfile(@Param("userId")
	Long userId,
		@Param("req")
		ProfileUpdateRequestDTO req);

	// 현재 비밀번호 조회
	String selectPasswordByUserId(Long userId);

	// 비밀번호 업데이트
	int updatePassword(
		@Param("userId")
		Long userId,
		@Param("password")
		String password);

	List<ProjectDTO> selectMyProjects(@Param("user_id")
	Long userId);
	// 유저 ID로 지갑 번호(ucl_id) 존재 여부 & 가져오기
	Long getWalletIdByUserId(@Param("userId")
	Long userId);

	// 계좌 연동하기
	void upsertUserWalletLink(
		@Param("userId")
		Long userId,
		@Param("walletId")
		Long walletId,
		@Param("accessToken")
		String randomAccessToken,
		@Param("refreshToken")
		String randomRefreshToken);
}
