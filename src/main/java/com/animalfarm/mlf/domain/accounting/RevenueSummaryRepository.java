package com.animalfarm.mlf.domain.accounting;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.accounting.dto.RevenueSummaryDTO;

@Mapper
public interface RevenueSummaryRepository {

	void insertSummary(RevenueSummaryDTO summary);

	List<Map<String, Object>> selectSettlementTargets(Map<String, Object> params);

}
