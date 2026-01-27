package com.animalfarm.mlf.domain.carbon;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.common.ApiResponseDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonOrderCompleteDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonOrderResponseDTO;

@RestController
@RequestMapping("/api/carbon")
public class CarbonController {

	@Autowired
	private CarbonService carbonService;

	// 전체 조회
	@GetMapping("/list")
	public List<CarbonListDTO> selectAll() {
		return carbonService.selectAll();
	}

	// category 조건 조회
	@GetMapping("/category")
	public List<CarbonListDTO> selectByCategory(
		@RequestParam(value = "category", required = false, defaultValue = "ALL")
		String category) {
		return carbonService.selectByCondition(category);
	}

	// 상세 페이지 조회
	@GetMapping("/{cpId}")
	public ApiResponseDTO<CarbonDetailDTO> selectDetail(@PathVariable
	Long cpId) {
		return carbonService.selectDetail(cpId);
	}

	@GetMapping("/orders/quote")
	public ApiResponseDTO<CarbonOrderResponseDTO> quote(
		@RequestParam("cpId")
		Long cpId,
		@RequestParam("amount")
		BigDecimal amount) {
		return carbonService.quoteOrder(cpId, amount);
	}

	@PostMapping("/orders/complete")
	public ApiResponseDTO<String> completeOrder(@RequestBody
	CarbonOrderCompleteDTO req) {
		carbonService.completeOrder(req); // 서비스에 구현
		return new ApiResponseDTO<>("주문 완료 처리 성공", null);
	}
}
