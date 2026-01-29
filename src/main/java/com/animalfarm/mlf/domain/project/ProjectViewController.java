package com.animalfarm.mlf.domain.project;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.animalfarm.mlf.domain.accounting.DividendService;
import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import com.animalfarm.mlf.domain.user.service.UserService;

@Controller
@RequestMapping("/project")
public class ProjectViewController {

	@Autowired
	ProjectService projectService;
	@Autowired
	DividendService dividendService;
	@Autowired
	UserService userService;

	@GetMapping({"", "/"})
	public String index() {
		return "redirect:/project/list";
	}

	@GetMapping("/{id}")
	public String selectDetail(@PathVariable
	Long id, Model model) {
		model.addAttribute("projectData", projectService.selectDetail(id));
		model.addAttribute("contentPage", "/WEB-INF/views/project/project_detail.jsp");
		model.addAttribute("myCash", projectService.selectMyWallet());
		return "layout";
	}

	@GetMapping("/list")
	public String projectListPage(Model model, ProjectSearchReqDTO searchReqDTO) {
		model.addAttribute("contentPage", "/WEB-INF/views/project/project_list.jsp");
		model.addAttribute("activeMenu", "project");
		model.addAttribute("projectList", projectService.selectByCondition(searchReqDTO));
		return "layout"; // 항상 layout.jsp를 리턴
	}

	@GetMapping("/list/fragment")
	public String projectListFragment(Model model, ProjectSearchReqDTO searchReqDTO) {
		model.addAttribute("projectList", projectService.selectByCondition(searchReqDTO));
		return "project/project_card_list";
	}

	@GetMapping("/dividend/poll")
	public String pollDividendType(Model model, @RequestParam
	Long id) {
		model.addAttribute("contentPage", "/WEB-INF/views/project/dividend_poll.jsp");
		DividendDTO dividend = dividendService.getDividendByID(id);
		model.addAttribute("dividend", dividendService.getDividendByID(id));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String formattedDate = dividend.getPollEndDate().format(formatter);
		model.addAttribute("pollEndDisplay", formattedDate);
		String curAddress = userService.selectAddress();
		model.addAttribute("curAddress", curAddress);
		return "layout";
	}
}
