package com.animalfarm.mlf.domain.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.token.dto.TokenDTO;

@RestController
public class TokenRestController {
	@Autowired
	TokenService tokenService;

	@GetMapping("/api/token/{projectId}")
	public TokenDTO selectDetail(@PathVariable("projectId")
	Long projectId) {
		return tokenService.selectByProjectId(projectId);
	}
}
