package com.animalfarm.mlf.domain.carbon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;

@Service
public class CarbonService {

	@Autowired
	private CarbonRepository carbonRepository;

	public List<CarbonListDTO> selectAll() {
		return carbonRepository.selectAll();
	}

	public List<CarbonListDTO> selectByCondition(String category) {
		return carbonRepository.selectByCondition(category);
	}
}
