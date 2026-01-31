package com.animalfarm.mlf.domain.accounting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;
import com.animalfarm.mlf.domain.accounting.dto.DividendRequestDTO;

@Mapper
public interface DividendRepository {

	void insertDividend(DividendDTO dividend);

	List<DividendDTO> selectPollingList(); // status가 POLLING이고 마감기한이 남은 유저 조회

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	void updateStatusToPolling(Long dividendId);

	DividendDTO selectById(Long dividendId);

	void updateUserSelection(@Param("dividendId")
	Long dividendId,
		@Param("dividendType")
		String dividendType);

	void updatePaidAt(List<? extends DividendRequestDTO> divReqDTOList);

	DividendDTO findAllDecidedForApi();

	void updateAutoDecide();

}
