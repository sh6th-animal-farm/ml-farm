package com.animalfarm.mlf.domain.accounting;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpenseRepository {

	void updateSummaryId(Map<String, Object> params);

}
