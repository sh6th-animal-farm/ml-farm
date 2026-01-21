package com.animalfarm.mlf.domain.mypage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageViewController {

	// /admin 또는 /admin/ 으로 접속 시 실행
	@GetMapping({"", "/"})
	public String index() {
		return "redirect:/mypage/profile";
	}

	@GetMapping("/profile")
	public String profilePage(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/mypage/profile.jsp");
		model.addAttribute("menu", "profile");
		return "layout";
	}

	@GetMapping("/project-history")
	public String projectHistoryPage(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/mypage/project_history.jsp");
		model.addAttribute("menu", "project");
		return "layout";
	}

	@GetMapping("/wallet")
	public String walletPage(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/mypage/wallet.jsp");
		model.addAttribute("menu", "wallet");
		return "layout";
	}

	@GetMapping("/transaction-history")
	public String transactionHistoryPage(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/mypage/transaction_history.jsp");
		model.addAttribute("menu", "transaction");
		return "layout";
	}

	@GetMapping("/carbon-history")
	public String carbonHistoryPage(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/mypage/carbon_history.jsp");
		model.addAttribute("menu", "carbon");
		return "layout";
	}
}
