package com.animalfarm.mlf.domain.refund;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefundRepository {

	public abstract int insertRefund(RefundDTO refundDTO);

}
