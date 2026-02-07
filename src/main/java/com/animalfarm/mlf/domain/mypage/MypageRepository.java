package com.animalfarm.mlf.domain.mypage;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.mypage.dto.CarbonHistoryDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileUpdateRequestDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProjectDTO;
import com.animalfarm.mlf.domain.mypage.dto.TokenInfoDTO;

@Mapper
public interface MypageRepository {

	List<CarbonHistoryDTO> selectCarbonHistoryByUserId(Long userId);

	ProfileDTO selectProfile(Long userId);

	int updateProfile(@Param("userId") Long userId, @Param("req") ProfileUpdateRequestDTO req);

	// 현재 비밀번호 조회
	String selectPasswordByUserId(Long userId);

	// 비밀번호 업데이트
	int updatePassword(
		@Param("userId")
		Long userId,
		@Param("password")
		String password);

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

	List<ProjectDTO> selectJoinedProjectCards(@Param("userId") Long userId, @Param("status") String status,
			@Param("limit") int limit, @Param("offset") int offset);

	long countJoinedProjectCards(@Param("userId") Long userId, @Param("status") String status);

	List<ProjectDTO> selectStarredProjectCards(@Param("userId") Long userId, @Param("status") String status,
			@Param("limit") int limit, @Param("offset") int offset);

	long countStarredProjectCards(@Param("userId") Long userId, @Param("status") String status);

	void upsertStarredProject(@Param("userId") Long userId, @Param("projectId") Long projectId,
			@Param("starred") boolean starred);

	// 거래 번호로 토큰 이름 및 종목 코드 조회
	List<TokenInfoDTO> findTokenInfoByTxId(@Param("txIdList") List<Long> txIdList);
}
