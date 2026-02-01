package com.animalfarm.mlf.domain.refund;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefundRepository {

	public abstract int insertRefund(RefundDTO refundDTO);

	public abstract List<RefundDTO> selectRefundInfoByWalletIds(@Param("walletIds") List<Long> walletIds,
			@Param("projectId") Long projectId,
			@Param("tokenId") Long tokenId);

}
