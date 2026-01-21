package com.animalfarm.mlf.domain.carbon;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/carbon")
public class CarbonViewController {

	/**
	 * [화면 전용] 탄소 배출권 상세 페이지 오픈
	 * - 화면 구성만 하고, 실제 데이터는 위의 RestController가 채웁니다.
	 */
	@GetMapping("/detail/{id}")
	public String carbonDetailPage(@PathVariable
	Long id, Model model) {
		model.addAttribute("cpId", id); // JS에서 API 호출할 때 쓰라고 넘겨줌
		model.addAttribute("contentPage", "/WEB-INF/views/carbon/carbon_detail.jsp");
		model.addAttribute("activeMenu", "carbon");
		return "layout";
	}
}
