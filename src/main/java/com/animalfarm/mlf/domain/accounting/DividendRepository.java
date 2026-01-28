package com.animalfarm.mlf.domain.accounting;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;

@Mapper
public interface DividendRepository {

	void insertDividend(DividendDTO dividend);

}
