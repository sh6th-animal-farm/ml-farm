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

//탄소 마켓 상세 조회 및 권한 제어 컨트롤러

@RestController
@RequestMapping("/api/carbon")
public class CarbonController {

	@Autowired
	private CarbonService carbonService;

	// 전체 조회
	@GetMapping("/")
	public List<CarbonListDTO> selectAll() {
		return carbonService.selectAll();
	}

	// category 조건 조회
	@GetMapping("/category")
	public List<CarbonListDTO> selectByCategory(
		@RequestParam(value = "category", required = false, defaultValue = "ALL")
		String category) {
		return carbonService.selectByCategory(category);
	}

	// 상세 페이지 조회
	@GetMapping("/{cpId}")
	public CarbonDetailDTO selectDetail(@PathVariable
	Long cpId) {
		return carbonService.selectDetail(cpId);
	}
}
