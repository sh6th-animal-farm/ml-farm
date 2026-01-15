package com.animalfarm.mlf.domain.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

	// /admin 또는 /admin/ 으로 접속 시 실행
	@GetMapping({"", "/"})
	public String index() {
		return "redirect:/admin/project/new";
	}

	@GetMapping("/project/new")
	public String newProject(Model model) {
		model.addAttribute("activeMenu", "project");
		return "admin/project_register";
	}

	@GetMapping("/farm/new")
	public String newFarm(Model model) {
		model.addAttribute("activeMenu", "farm");
		return "admin/farm_register";
	}

	@GetMapping("/cultivation/new")
	public String newCultivation(Model model) {
		model.addAttribute("activeMenu", "cultivation");
		return "admin/cultivation_register";
	}

	@GetMapping("/revenue/new")
	public String newIncome(Model model) {
		model.addAttribute("activeMenu", "revenue");
		return "admin/revenue_register";
	}

	@GetMapping("/expense/new")
	public String newExpenditure(Model model) {
		model.addAttribute("activeMenu", "expense");
		return "admin/expense_register";
	}
}
