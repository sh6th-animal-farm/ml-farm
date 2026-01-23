package com.animalfarm.mlf.domain.carbon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;

@RestController
@RequestMapping("/carbon")
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

	// 상세 페이지 조회
	@GetMapping("/detail/{cpId}")
	public CarbonDetailDTO selectDetail(@PathVariable
	Long cpId) {
		return carbonService.selectDetail(cpId);
	}
}
