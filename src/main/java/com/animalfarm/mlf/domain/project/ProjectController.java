package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectPictureDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStarredDTO;

@RestController
public class ProjectController {
	@Autowired
	ProjectService projectService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// 억지로 객체 만들지 말고, 들어오는 문자열 그대로 처리하라고 스프링한테 명령
		binder.registerCustomEditor(java.time.OffsetDateTime.class, new java.beans.PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				// "2026-01-20T20:35" -> "2026-01-20T20:35:00+09:00" 강제 변환
				setValue(
					java.time.LocalDateTime.parse(text).atZone(java.time.ZoneId.of("Asia/Seoul")).toOffsetDateTime());
			}
		});
	}

	@GetMapping("/projects")
	public String projectListPage() {
		return "";
	}

	@GetMapping("/api/project/{projectId}")
	public ProjectDetailDTO selectDetail(@PathVariable("projectId")
	Long projectId) {
		return projectService.selectDetail(projectId);
	}

	@GetMapping("/api/projects/all")
	public List<ProjectDTO> selectAll() {
		return projectService.selectAll();
	}

	@GetMapping("/api/projects")
	public List<ProjectListDTO> selectByCondition(@ModelAttribute
	ProjectSearchReqDTO searchDTO) {
		return projectService.selectByCondition(searchDTO);
	}

	//관심 프로젝트 신규 등록
	@PostMapping("/api/projects/starred/interest")
	public String upsertStrarredProject(@ModelAttribute
	ProjectStarredDTO projectStarredDTO) {
		String message = null;
		if (projectService.upsertStrarredProject(projectStarredDTO)) {
			message = "success";
		} else {
			message = "fail";
		}
		return message;
	}
	@PostMapping("/api/projects/insert")
	public ResponseEntity<String> insertProject(@RequestBody
	ProjectInsertDTO projectInsertDTO) {
		if (projectService.insertProject(projectInsertDTO)) {
			return ResponseEntity.ok("success");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로젝트 등록 중 서버 오류가 발생했습니다.");
		}

	}

	@PostMapping("/api/projects/update")
	public ResponseEntity<String> updateProject(@RequestBody
	ProjectDTO projectDTO) {
		if (projectService.updateProject(projectDTO)) {
			return ResponseEntity.ok("success");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업데이트 중 서버 오류가 발생했습니다.");
		}
	}

	@GetMapping("/api/project/picture/{projectId}/all")
	public List<ProjectPictureDTO> selectPictures(@PathVariable("projectId")
	Long projectId) {
		return projectService.selectPictures(projectId);
	}

}
