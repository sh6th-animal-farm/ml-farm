package com.animalfarm.mlf.domain.token;

import java.math.BigDecimal;
import java.util.List;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.token.dto.MarketDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.animalfarm.mlf.common.security.SecurityUtil;

@Controller
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenViewController {

	@Autowired
	private TokenService tokenService;
	private final ExternalApiUtil externalApiUtil;

	@GetMapping
	public String tokenListPage(Model model) {
		List<MarketDTO> list = tokenService.selectAll();
		if (!list.isEmpty()) {
			System.out.println("Data Type: " + list.get(0).getClass().getName());
		}
		model.addAttribute("tokenList", tokenService.selectAll());
		model.addAttribute("contentPage", "/WEB-INF/views/token/token_list.jsp");

		return "layout";
	}

	@GetMapping("/{id}}")
	public String tokenDetailPage(@PathVariable Long id, Model model) {
		Long userId = SecurityUtil.getCurrentUserId();

		model.addAttribute("tokenId", id);
		model.addAttribute("tokenDetail", tokenService.selectByTokenId(id));
		model.addAttribute("contentPage", "/WEB-INF/views/token/token_detail.jsp");

		return "layout";
	}
}
