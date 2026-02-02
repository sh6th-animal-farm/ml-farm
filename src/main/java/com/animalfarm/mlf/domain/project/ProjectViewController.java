package com.animalfarm.mlf.domain.project;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.animalfarm.mlf.domain.accounting.DividendService;
import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import com.animalfarm.mlf.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectViewController {
	
	@Value("${api.kakako.javascript.key}")
	String kakaoMapKey; 

	private final ProjectService projectService;
	private final ObjectMapper objectMapper;
	
	@Autowired
	DividendService dividendService;
	@Autowired
	UserService userService;
	
	@GetMapping({"", "/"})
	public String index() {
		return "redirect:/project/list";
	}

	@GetMapping("/{id}")
	public Object selectDetail(@PathVariable
	Long id, Model model, @RequestHeader(value = "X-Requested-With", required = false)
	String requestedWith) {

		if ("fetch".equals(requestedWith)) {
			Double balance = projectService.selectMyWalletAmount();
			return ResponseEntity.ok(balance); // 여기서 서비스 호출!
		}

		model.addAttribute("projectData", projectService.selectDetail(id));
		model.addAttribute("contentPage", "/WEB-INF/views/project/project_detail.jsp");
		//model.addAttribute("myCash", projectService.selectMyWalletAmount());
		model.addAttribute("activeMenu", "project");
		return "layout";
	}

	@GetMapping("/list")
	public String projectListPage(Model model, ProjectSearchReqDTO searchReqDTO) throws Exception {
		model.addAttribute("contentPage", "/WEB-INF/views/project/project_list.jsp");
		model.addAttribute("activeMenu", "project");
		List<ProjectListDTO> projectList = projectService.selectByCondition(searchReqDTO);
	    String projectListJson = objectMapper.writeValueAsString(projectList);
		model.addAttribute("projectList", projectList);
		model.addAttribute("projectListJson", projectListJson);
		model.addAttribute("kakaoMapKey", kakaoMapKey);
		return "layout"; // 항상 layout.jsp를 리턴
	}

	@GetMapping("/list/fragment")
	public String projectListFragment(Model model, ProjectSearchReqDTO searchReqDTO) {
		model.addAttribute("projectList", projectService.selectByCondition(searchReqDTO));
		return "project/project_card_list";
	}

	@GetMapping("/list/fragment/main")
	public String projectListFragmentForMain(Model model) {
		model.addAttribute("projectList", projectService.selectByConditionForMain());
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
		return "layout";
	}
}
