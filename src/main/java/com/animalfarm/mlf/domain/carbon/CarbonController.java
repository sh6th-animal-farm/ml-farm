package com.animalfarm.mlf.domain.carbon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;

@RestController
public class CarbonController {

	@Autowired
	private CarbonService carbonService;

	// 전체 조회
	@GetMapping("/api/carbon")
	public List<CarbonListDTO> selectAll() {
		return carbonService.selectAll();
	}

	// category 조건 조회
	@GetMapping("/api/carbon/category")
	public List<CarbonListDTO> selectByCategory(
		@RequestParam(value = "category", required = false, defaultValue = "ALL")
		String category) {
		return carbonService.selectByCategory(category);
	}
}
