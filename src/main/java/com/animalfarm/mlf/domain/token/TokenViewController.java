package com.animalfarm.mlf.domain.token;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.animalfarm.mlf.common.security.SecurityUtil;

@Controller
@RequestMapping("/token")
public class TokenViewController {

	@Autowired
	private TokenService tokenService;

	@GetMapping("/list")
	public String tokenListPage(Model model) {
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
