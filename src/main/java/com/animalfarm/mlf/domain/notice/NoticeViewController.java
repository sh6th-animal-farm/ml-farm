package com.animalfarm.mlf.domain.notice;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
public class NoticeViewController {

	@GetMapping({"", "/"})
	public String index() {
		return "redirect:/notice/list";
	}
	
	@GetMapping("/list")
	public String noticeListPage(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/notice/notice.jsp");
		model.addAttribute("activeMenu", "notice");
		return "layout";
	}
	
}
